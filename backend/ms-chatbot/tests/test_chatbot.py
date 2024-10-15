import unittest
import requests
import os
from unittest.mock import patch
from src.app import app

class ChatbotTestCase(unittest.TestCase):

    def setUp(self):
        self.app = app.test_client()
        self.app.testing = True

    @patch('src.chatbot.ChatbotResource.is_authenticated')
    def test_is_authenticated_invalid_token(self, mock_is_authenticated):
        # Simular que la autenticación falla
        mock_is_authenticated.return_value = None

        # Token no empieza con 'Bearer '
        response = self.app.post('/chat', 
                                json={"message": "crear incidencia", "originType": "web", "userId": "123"},
                                headers={"Authorization": "InvalidToken"})
        self.assertEqual(response.status_code, 401)
        self.assertIn("Usuario no autenticado", response.get_json()['message'])

    @patch('src.chatbot.requests.get')
    def test_is_authenticated_no_token(self, mock_get):
        # Simular que no se envía un token
        token = None
        response = self.app.post('/chat', 
                                 json={"message": "crear incidencia"},
                                 headers={"Authorization": token})
        self.assertEqual(response.status_code, 401)
        mock_get.assert_not_called()

    @patch('src.chatbot.requests.get')
    def test_is_authenticated_invalid_token_format(self, mock_get):
        # Simular que se envía un token mal formado
        token = "InvalidToken"
        response = self.app.post('/chat', 
                                 json={"message": "crear incidencia"},
                                 headers={"Authorization": token})
        self.assertEqual(response.status_code, 401)
        mock_get.assert_not_called()

    @patch('src.chatbot.requests.get')
    def test_is_authenticated_success(self, mock_get):
        os.environ['USERS_SERVICE_URL'] = 'http://localhost:5000'

        # Simular una respuesta exitosa del microservicio de usuarios
        token = "Bearer validtoken"
        mock_get.return_value.status_code = 200
        mock_get.return_value.json.return_value = {"id": "123"}

        response = self.app.post('/chat', 
                                 json={"message": "crear incidencia", "originType": "web"},
                                 headers={"Authorization": token})
        self.assertEqual(response.status_code, 200)
        mock_get.assert_called_once_with('http://localhost:5000/me', headers={'Authorization': token})

    @patch('src.chatbot.requests.get')
    def test_is_authenticated_invalid_response(self, mock_get):
        os.environ['USERS_SERVICE_URL'] = 'http://localhost:5000'
        # Simular una respuesta fallida del microservicio (código diferente a 200)
        token = "Bearer invalidtoken"
        mock_get.return_value.status_code = 401  # Simulando token inválido

        response = self.app.post('/chat', 
                                 json={"message": "crear incidencia", "originType": "web"},
                                 headers={"Authorization": token})
        self.assertEqual(response.status_code, 401)
        mock_get.assert_called_once_with('http://localhost:5000/me', headers={'Authorization': token})

    @patch('src.chatbot.requests.get', side_effect=requests.RequestException("Network Error"))
    def test_is_authenticated_network_error(self, mock_get):
        os.environ['USERS_SERVICE_URL'] = 'http://localhost:5000'
        # Simular una excepción durante la solicitud HTTP (problema de red)
        token = "Bearer validtoken"
        response = self.app.post('/chat', 
                                 json={"message": "crear incidencia"},
                                 headers={"Authorization": token})
        self.assertEqual(response.status_code, 401)
        mock_get.assert_called_once_with('http://localhost:5000/me', headers={'Authorization': token})

    @patch('src.chatbot.ChatbotResource.is_authenticated')
    @patch('src.chatbot.ChatbotResource.create_incident')
    def test_create_incident(self, mock_create_incident, mock_is_authenticated):
        # Simular que la autenticación es exitosa
        mock_is_authenticated.return_value = {"id": "123"}

        # Simular el comportamiento de la creación de una incidencia
        mock_create_incident.return_value = {"message": "Para crear una incidencia, necesito más información. Te pediré en diferentes mensajes la información que necesito. Por favor, indícame en el siguiente mensaje el asunto de la incidencia."}
        
        # Iniciar la creación de la incidencia
        response = self.app.post('/chat', 
                                 json={"message": "crear incidencia", "originType": "web", "userId": "123"},
                                 headers={"Authorization": "Bearer testtoken"})
        self.assertEqual(response.status_code, 200)
        self.assertIn('Para crear una incidencia, necesito más información. Te pediré en diferentes mensajes la información que necesito. Por favor, indícame en el siguiente mensaje el asunto de la incidencia.', response.get_json()['message'])


        mock_create_incident.return_value = {"message": "Gracias. Ahora, como último paso, dime la descripción de la incidencia en un solo mensaje."}
        # Enviar el asunto
        response = self.app.post('/chat', 
                                 json={"message": "Problema en el servidor", "originType": "web", "userId": "123"},
                                 headers={"Authorization": "Bearer testtoken"})
        self.assertEqual(response.status_code, 200)
        self.assertIn('Gracias. Ahora, como último paso, dime la descripción de la incidencia en un solo mensaje.', response.get_json()['message'])

        mock_create_incident.return_value = {"message": "Incidencia creada con éxito", "incidentId": 123}
        # Enviar la descripción
        response = self.app.post('/chat', 
                                 json={"message": "No puedo acceder al sistema", "originType": "web", "userId": "123"},
                                 headers={"Authorization": "Bearer testtoken"})
        self.assertEqual(response.status_code, 200)
        self.assertIn('Incidencia creada con éxito', response.get_json()['message'])
        self.assertEqual(response.get_json()['incidentId'], 123)

    @patch('src.chatbot.ChatbotResource.is_authenticated')
    @patch('src.chatbot.ChatbotResource.create_incident')
    def test_create_incident_failure(self, mock_create_incident, mock_is_authenticated):
        # Simular que la autenticación es exitosa
        mock_is_authenticated.return_value = {"id": "123"}

        mock_create_incident.return_value = {"message": "Hubo un error al crear la incidencia", "details": {}}

        # Iniciar la creación de la incidencia
        response = self.app.post('/chat', 
                                 json={"message": "crear incidencia", "originType": "web", "userId": "123"},
                                 headers={"Authorization": "Bearer testtoken"})
        self.assertEqual(response.status_code, 200)

        # Enviar el asunto
        response = self.app.post('/chat', 
                                 json={"message": "Problema en el servidor", "originType": "web", "userId": "123"},
                                 headers={"Authorization": "Bearer testtoken"})
        self.assertEqual(response.status_code, 200)

        # Enviar la descripción (fallo en creación de incidencia)
        response = self.app.post('/chat', 
                                 json={"message": "No puedo acceder al sistema", "originType": "web", "userId": "123"},
                                 headers={"Authorization": "Bearer testtoken"})
        self.assertEqual(response.status_code, 200)
        self.assertIn('Hubo un error al crear la incidencia', response.get_json()['message'])

    @patch('src.chatbot.ChatbotResource.is_authenticated')
    @patch('src.chatbot.requests.post')
    def test_create_incident_error(self, mock_post, mock_is_authenticated):
        # Simular que la autenticación es exitosa
        mock_is_authenticated.return_value = {"id": "123"}

        # Simular un error al crear la incidencia (respuesta con código diferente a 201)
        mock_post.return_value.status_code = 400
        mock_post.return_value.json.return_value = {"error": "Invalid request"}

        # Simular el flujo de conversación hasta llegar a la creación de la incidencia
        response = self.app.post('/chat', 
                                json={"message": "crear incidencia", "originType": "web", "userId": "123"},
                                headers={"Authorization": "Bearer testtoken"})
        self.assertEqual(response.status_code, 200)

        response = self.app.post('/chat', 
                                json={"message": "Problema en el servidor", "originType": "web", "userId": "123"},
                                headers={"Authorization": "Bearer testtoken"})
        self.assertEqual(response.status_code, 200)

        response = self.app.post('/chat', 
                                json={"message": "No puedo acceder al sistema", "originType": "web", "userId": "123"},
                                headers={"Authorization": "Bearer testtoken"})
        self.assertEqual(response.status_code, 200)
        self.assertIn('Hubo un error al crear la incidencia', response.get_json()['message'])

    @patch('src.chatbot.ChatbotResource.is_authenticated')
    @patch('src.chatbot.requests.post', side_effect=requests.exceptions.RequestException("Network Error"))
    def test_create_incident_network_error(self, mock_post, mock_is_authenticated):
        # Simular que la autenticación es exitosa
        mock_is_authenticated.return_value = {"id": "123"}

        # Simular un error de red al intentar crear la incidencia
        response = self.app.post('/chat', 
                                json={"message": "crear incidencia", "originType": "web", "userId": "123"},
                                headers={"Authorization": "Bearer testtoken"})
        self.assertEqual(response.status_code, 200)

        response = self.app.post('/chat', 
                                json={"message": "Problema en el servidor", "originType": "web", "userId": "123"},
                                headers={"Authorization": "Bearer testtoken"})
        self.assertEqual(response.status_code, 200)

        response = self.app.post('/chat', 
                                json={"message": "No puedo acceder al sistema", "originType": "web", "userId": "123"},
                                headers={"Authorization": "Bearer testtoken"})
        self.assertEqual(response.status_code, 200)
        self.assertIn('No se pudo conectar con el servicio de incidencias', response.get_json()['message'])

    @patch('src.chatbot.ChatbotResource.is_authenticated')
    def test_origin_type_missing(self, mock_is_authenticated):
        # Simular que la autenticación es exitosa
        mock_is_authenticated.return_value = {"id": "123"}

        # Verificar que 'originType' es obligatorio
        response = self.app.post('/chat', 
                                 json={"message": "crear incidencia", "userId": "123"},
                                 headers={"Authorization": "Bearer testtoken"})
        self.assertEqual(response.status_code, 400)
        self.assertIn("El campo 'originType' es obligatorio", response.get_json()['message'])

    @patch('src.chatbot.ChatbotResource.is_authenticated')
    def test_unauthenticated_user(self, mock_is_authenticated):
        # Simular que la autenticación falla
        mock_is_authenticated.return_value = None

        # Verificar que el usuario no autenticado recibe el mensaje de error correcto
        response = self.app.post('/chat', json={"message": "crear incidencia"})
        self.assertEqual(response.status_code, 401)
        self.assertIn("Usuario no autenticado", response.get_json()['message'])

    @patch('src.chatbot.ChatbotResource.is_authenticated')
    def test_chatterbot_response(self, mock_is_authenticated):
        # Simular que la autenticación es exitosa
        mock_is_authenticated.return_value = {"id": "123"}

        response = self.app.post('/chat', 
                                json={"message": "Hola", "originType": "app", "userId": "123"},
                                headers={"Authorization": "Bearer testtoken"})
        self.assertEqual(response.status_code, 200)
        self.assertIn('Hola', response.get_json()['message'])

    @patch('src.chatbot.ChatbotResource.is_authenticated')
    def test_conversation_already_active(self, mock_is_authenticated):
        # Simular que la autenticación es exitosa
        mock_is_authenticated.return_value = {"id": "123"}

        # Iniciar la creación de la incidencia
        response = self.app.post('/chat', 
                                 json={"message": "crear incidencia", "originType": "web", "userId": "123"},
                                 headers={"Authorization": "Bearer testtoken"})
        self.assertEqual(response.status_code, 200)
        self.assertIn('Para crear una incidencia, necesito más información. Te pediré en diferentes mensajes la información que necesito. Por favor, indícame en el siguiente mensaje el asunto de la incidencia.', response.get_json()['message'])

        # Simular que la conversación está en curso (esperando asunto)
        response = self.app.post('/chat', 
                                 json={"message": "Problema en el servidor", "originType": "web", "userId": "123"},
                                 headers={"Authorization": "Bearer testtoken"})
        self.assertEqual(response.status_code, 200)
        self.assertIn('Gracias. Ahora, como último paso, dime la descripción de la incidencia en un solo mensaje.', response.get_json()['message'])

    
    @patch('src.chatbot.ChatbotResource.is_authenticated')
    def test_missing_message(self, mock_is_authenticated):
        # Simular que la autenticación es exitosa
        mock_is_authenticated.return_value = {"id": "123"}

        # Verificar que 'message' es obligatorio
        response = self.app.post('/chat', 
                                 json={"originType": "web", "userId": "123"},
                                 headers={"Authorization": "Bearer testtoken"})
        self.assertEqual(response.status_code, 400)
