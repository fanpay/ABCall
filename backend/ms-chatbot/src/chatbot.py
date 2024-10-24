import os
import requests
from flask import request
from flask_restful import Resource
from dotenv import load_dotenv

# Cargar las variables de entorno
load_dotenv()

# Diccionario global para almacenar el estado de las conversaciones
conversations = {}

class ChatbotResource(Resource):
    def __init__(self):
        # URL del microservicio de incidentes y usuarios
        self.incident_service_url = os.getenv('INCIDENT_SERVICE_URL')
        self.users_service_url = os.getenv('USERS_SERVICE_URL')
    
    def load_rules(self, language):
        rules = {}
        file_path = f'src/rules_{language}.txt'  # Archivo de reglas en el idioma correspondiente
        
        try:
            with open(file_path, 'r') as file:
                for line in file:
                    if ':' in line:
                        key, value = line.strip().split(':', 1)
                        rules[key.strip().lower()] = value.strip()
        except FileNotFoundError:
            print(f"El archivo de reglas no se encontró para el idioma {language}.")
        return rules

    def post(self):
        token = request.headers.get('Authorization')
        user_data = self.is_authenticated(token)
    
        if not user_data:
            return {"message": "Usuario no autenticado"}, 401
        
        if not request.json.get('message'):
            return {"message": "El campo 'message' es obligatorio"}, 400
    
        user_id = user_data['id']

        # Obtener el idioma deseado (por defecto 'es' para español)
        language = request.json.get('lang', 'es').lower()

        # Cargar las reglas correspondientes al idioma solicitado
        self.rules = self.load_rules(language)
        
        # Obtener el tipo de origen (web, móvil, etc.)
        origin_type = request.json.get('originType', '').lower()
            
        if not origin_type:
            return {"message": "El campo 'originType' es obligatorio"}, 400
    
        # Recibir el mensaje del usuario
        user_input = request.json.get('message', '').lower()

        # Verificar si el usuario ya está en el proceso de crear una incidencia
        if user_id in conversations:
            return self.handle_incident_creation(user_id, user_input)
        
        # Si el usuario inicia la creación de una incidencia
        if any(phrase in user_input for phrase in ["crear incidencia", "create incident"]):
            conversations[user_id] = {"status": "awaiting_subject", "originType": origin_type}
            return {"message": self.rules.get("create_incident", "Para crear una incidencia, necesito más información. Te pediré en diferentes mensajes la información que necesito. Por favor, indícame en el siguiente mensaje el asunto de la incidencia.")}


        # Buscar en las reglas de respuesta en el idioma seleccionado
        if user_input in self.rules:
            return {"message": self.rules[user_input]}
        else:
            return {"message": self.rules.get("default", "Lo siento, no entiendo lo que dices.")}

    def handle_incident_creation(self, user_id, user_input):
        conversation = conversations[user_id]

        if conversation["status"] == "awaiting_subject":
            # Guardar el asunto y cambiar el estado
            conversation["subject"] = user_input
            conversation["status"] = "awaiting_description"
            return {"message": self.rules.get("awaiting_description", "Gracias. Ahora, dime la descripción de la incidencia.")}

        elif conversation["status"] == "awaiting_description":
            # Guardar la descripción y proceder a crear la incidencia
            conversation["description"] = user_input
            return self.create_incident(user_id)

    def create_incident(self, user_id):
        # Obtener los datos de la conversación
        conversation = conversations.get(user_id)

        # Datos de la incidencia
        incident_data = {
            "userId": user_id,
            "subject": conversation.get("subject"),
            "description": conversation.get("description"),
            "originType": conversation.get("originType")
        }

        try:
            response = requests.post(self.incident_service_url, json=incident_data)

            # Verificar si la incidencia fue creada correctamente
            if response.status_code == 201:
                del conversations[user_id]  # Eliminar el estado de la conversación
                incident_id = response.json().get('incidentId')
                return {"message": self.rules.get("incident_created", "Incidencia creada con éxito") + f" ID: {incident_id}"}
            else:
                return {"message": self.rules.get("incident_creation_failed", "Hubo un error al crear la incidencia."), "details": response.json()}

        except requests.exceptions.RequestException as e:
            return {"message": self.rules.get("service_unavailable", "No se pudo conectar con el servicio de incidencias."), "details": str(e)}

    def is_authenticated(self, token):
        if not token:
            return None
    
        if not token.startswith("Bearer "):
            return None

        try:
            headers = {"Authorization": token}
            response = requests.get(self.users_service_url + '/me', headers=headers)

            if response.status_code == 200:
                user_data = response.json()
                return user_data
            else:
                return None
        except requests.RequestException as e:
            print(f"Error al conectar con el microservicio de usuarios: {e}")
            return None
