import unittest
from src.blueprints.ping import (
    ping_blueprint,
)  # Importa el blueprint que contiene la función a probar
from flask import Flask


class TestPingEndpoint(unittest.TestCase):

    def setUp(self):
        # Configura una aplicación Flask de prueba y registra el blueprint
        self.app = Flask(__name__)
        self.app.register_blueprint(ping_blueprint)
        self.client = self.app.test_client()

    def test_ping_endpoint(self):
        # Realiza una solicitud GET al endpoint /offers/ping
        response = self.client.get("/offers/ping")

        # Verifica el código de estado de la respuesta
        self.assertEqual(response.status_code, 200)

        # Verifica el contenido de la respuesta
        self.assertEqual(response.data.decode("utf-8"), "pong")
