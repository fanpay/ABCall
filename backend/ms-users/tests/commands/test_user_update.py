import unittest
from unittest.mock import patch
import uuid

from src.commands.user_update import ViewUserUpdate
from src.errors.errors import UserBadRequestError, UserNotFoundError
from src.models.user import User
from faker import Faker


class TestViewUserUpdate(unittest.TestCase):
    def setUp(self):
        self.fake = Faker()

    @patch('os.environ', {
        "SECRET_TOKEN": "test",
        "TRUENATIVE_PATH": "http://truenative_path",
        "USERS_PATH": "http://user_path",
    })
    def test_validate_data(self):
        # Datos de ejemplo para la prueba
        valid_data = {
            "dni": self.fake.numerify(text="#######"),
            "fullName": self.fake.name(),
            "phoneNumber": self.fake.phone_number(),
            "status": "POR_VERIFICAR",
            "RUV": self.fake.phone_number(),
            "score": 100,
            "verifyToken": "qwerty"
        }
        invalid_data = {
            "username": self.fake.user_name(),
            "password": self.fake.color_name(),
            "email": self.fake.email(),
        }

        # Parchea el método validate_data y validate_existing_user para que no levante una excepción
        with patch.object(ViewUserUpdate, "validate_data", return_value=None), \
            patch.object(ViewUserUpdate, "validate_existing_user", return_value=None), \
         patch.object(ViewUserUpdate, "validate_token_webhook", return_value=None):
            # Crea una instancia de ViewUserUpdate
            random_id = str(uuid.uuid4())
            view_signin = ViewUserUpdate(valid_data, random_id)

        # Verificar que no se lance una excepción con datos válidos
        #self.assertIsNone(view_signin.validate_data(valid_data))

        # Verificar que se lance una excepción UserBadRequestError con datos inválidos
        with self.assertRaises(UserBadRequestError):
            view_signin.validate_data(invalid_data)

    @patch("src.commands.user_update.User")
    def test_validate_existing_user(self, mock_user):
        # Datos de ejemplo para la prueba
        data_json = {
            "dni": self.fake.numerify(text="#######"),
            "fullName": self.fake.name(),
            "phoneNumber": self.fake.phone_number(),
            "status": "POR_VERIFICAR",
        }

        random_id = str(uuid.uuid4())

        # Se configura el comportamiento esperado de los mocks
        mock_user.query.filter().first.return_value = None

        # Simula que el usuario no está registrado
        mock_user.query.filter(User.id == random_id).first.return_value = None

        with patch.object(ViewUserUpdate, "validate_data", return_value=None):

            with self.assertRaises(UserNotFoundError):
                ViewUserUpdate(data_json, random_id)
