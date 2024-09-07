import unittest
from unittest.mock import MagicMock, patch
from flask import Flask
from src.blueprints.reset import reset_blueprint


class TestResetEndpoint(unittest.TestCase):

    def setUp(self):
        # Configurar una aplicación Flask de prueba
        self.app = Flask(__name__)

        # Configurar la aplicación Flask con una base de datos en memoria para pruebas
        self.app.config["SQLALCHEMY_DATABASE_URI"] = "sqlite:///:memory:"
        self.app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False

        # Inicializar la instancia de SQLAlchemy con la aplicación de prueba
        from src.extensions import db

        db.init_app(self.app)

        # Registrar el blueprint de reset en la aplicación Flask de prueba
        self.app.register_blueprint(reset_blueprint)

        # Crear un cliente de prueba para realizar solicitudes HTTP
        self.client = self.app.test_client()

    def test_reset_endpoint(self):
        # Mock para ResetOffers
        mock_reset_offers = MagicMock()
        mock_reset_offers.execute.return_value = {
            "msg": "Todos los datos fueron eliminados"
        }

        # Simula una solicitud POST al endpoint /offers/reset
        with self.app.test_request_context("/offers/reset", method="POST"):
            # Establece el mock de ResetOffers en la vista
            with patch(
                "src.blueprints.reset.ResetOffers", return_value=mock_reset_offers
            ):
                # Realiza la solicitud POST
                response = self.client.post("/offers/reset")

                # Verifica el código de estado de la respuesta
                self.assertEqual(response.status_code, 200)

                # Verifica el contenido de la respuesta
                self.assertEqual(
                    response.json, {"msg": "Todos los datos fueron eliminados"}
                )
