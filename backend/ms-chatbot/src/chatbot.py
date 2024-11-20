import os
import requests
from flask import request
from flask_restful import Resource
from dotenv import load_dotenv
import unicodedata

# Cargar las variables de entorno
load_dotenv()

# Diccionario global para almacenar el estado de las conversaciones
conversations = {}

# Función para normalizar texto eliminando acentos
def normalize_text(text):
    normalized_text = ''.join(
        c for c in unicodedata.normalize('NFD', text)
        if unicodedata.category(c) != 'Mn'
    )
    return normalized_text.lower()

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
                        # Normalizar clave al cargar las reglas para facilitar las coincidencias
                        rules[normalize_text(key.strip())] = value.strip()
        except FileNotFoundError:
            print(f"El archivo de reglas no se encontró para el idioma {language}.")
        return rules

    def post(self):
        token = request.headers.get('Authorization')
        user_data = self.is_authenticated(token)

        if not user_data:
            return {"message": "Usuario no autenticado"}, 401

        message = request.json.get('message')
        if not message:
            return {"message": "Por favor, introduce un mensaje."}, 400

        # Normalizar entrada del usuario
        user_input = normalize_text(message.strip())
        user_id = user_data['id']
        language = request.json.get('lang', 'es').lower()
        self.rules = self.load_rules(language)
        origin_type = request.json.get('originType', '').lower()

        if not origin_type:
            return {"message": "El campo 'originType' es obligatorio"}, 400

        # Mantener el historial de mensajes y estado de conversación
        if user_id not in conversations:
            conversations[user_id] = {"status": None, "originType": origin_type, "messages": []}

        conversation = conversations[user_id]
        conversation["messages"].append({"role": "user", "content": user_input})

        # Verificar si el usuario desea iniciar la creación de incidencia en cualquier momento
        if user_input in ["crear incidencia", "create incident"]:
            conversation["status"] = "awaiting_subject"
            return {"message": self.rules.get("create_incident", "Para crear una incidencia, necesito más información. Por favor, indícame en el siguiente mensaje el asunto de la incidencia.")}

        # Responder con mensajes específicos de reglas
        if user_input in self.rules:
            response_message = self.rules[user_input]
            conversation["messages"].append({"role": "bot", "content": response_message})
            return {"message": response_message}

        # Proponer creación de incidencia si no hay una sugerencia
        no_solution_msg = self.rules.get("no_solution", "No encontré una solución. ¿Deseas crear una incidencia para recibir soporte adicional?")
        
        if conversation["status"] is None:
            conversation["status"] = "awaiting_incident_confirmation"
            conversation["messages"].append({"role": "bot", "content": no_solution_msg})
            return {"message": no_solution_msg}

        # Manejo de la confirmación para crear la incidencia
        if conversation["status"] == "awaiting_incident_confirmation":
            if user_input in ["sí", "si", "yes"]:
                conversation["status"] = "awaiting_subject"
                return {"message": self.rules.get("create_incident", "Para crear una incidencia, necesito más información. Por favor, indícame en el siguiente mensaje el asunto de la incidencia.")}
            else:
                conversation["status"] = None
                return {"message": "Entendido. Si necesitas más ayuda, estoy aquí para ayudarte."}

        return self.handle_incident_creation(user_id, user_input)

    def handle_incident_creation(self, user_id, user_input):
        conversation = conversations[user_id]

        if conversation["status"] == "awaiting_subject":
            conversation["subject"] = user_input
            conversation["status"] = "awaiting_description"
            return {"message": self.rules.get("awaiting_description", "Gracias. Ahora, dime la descripción de la incidencia.")}

        elif conversation["status"] == "awaiting_description":
            conversation["description"] = user_input
            return self.create_incident(user_id)

    def create_incident(self, user_id):
        conversation = conversations.get(user_id)

        incident_data = {
            "userId": user_id,
            "subject": conversation.get("subject"),
            "description": conversation.get("description"),
            "originType": conversation.get("originType")
        }

        try:
            response = requests.post(self.incident_service_url, json=incident_data)

            if response.status_code == 201:
                del conversations[user_id]
                incident_id = response.json().get('incidentId')
                return {"message": self.rules.get("incident_created", "Incidencia creada con éxito") + f" ID: {incident_id}"}
            else:
                return {"message": self.rules.get("incident_creation_failed", "Hubo un error al crear la incidencia."), "details": response.json()}

        except requests.exceptions.RequestException as e:
            return {"message": self.rules.get("service_unavailable", "No se pudo conectar con el servicio de incidencias."), "details": str(e)}

    def is_authenticated(self, token):
        if not token or not token.startswith("Bearer "):
            return None

        try:
            headers = {"Authorization": token}
            response = requests.get(self.users_service_url + '/me', headers=headers)

            if response.status_code == 200:
                return response.json()
            else:
                return None
        except requests.RequestException as e:
            print(f"Error al conectar con el microservicio de usuarios: {e}")
            return None
