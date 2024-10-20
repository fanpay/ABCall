import datetime
import unittest
from unittest.mock import patch
import uuid

from src.commands.user_info import ViewUserInfo
from src.errors.errors import TokenNotFoundError, TokenNotValidError
from src.models.user import User
from faker import Faker


class TestViewUserInfo(unittest.TestCase):
    def setUp(self):
        self.fake = Faker()

    def test_validate_token(self):
        # Datos de ejemplo para la prueba
        valid_access_token = "Bearer " + str(uuid.uuid4())

        # Parchea el método validate_data y validate_existing_user para que no levante una excepción
        with patch.object(
            ViewUserInfo, "validate_token", return_value=None
        ), patch.object(ViewUserInfo, "validate_existing_user", return_value=None):

            view_user_info = ViewUserInfo(valid_access_token)

        # Verificar que no se lance una excepción con datos válidos
        self.assertIsNone(view_user_info.validate_token(valid_access_token))

        # Verificar que se lance una excepción UserBadRequestError con datos inválidos
        with self.assertRaises(TokenNotFoundError):
            view_user_info.validate_token(None)

    @patch("src.commands.user_info.User")
    def test_validate_existing_user(self, mock_user):
        # Datos de ejemplo para la prueba
        valid_access_token = str(uuid.uuid4())

        # Se configura el comportamiento esperado de los mocks
        mock_user.query.filter().first.return_value = None

        # Simula que el usuario no está registrado
        mock_user.query.filter(User.token == valid_access_token).first.return_value = (
            None
        )

        # Verificar que se lance la excepción TokenNotValidError al crear una instancia de ViewUserInfo cuando no existe un usuario con ese token
        with patch.object(ViewUserInfo, "validate_token", return_value=None):
            with self.assertRaises(TokenNotValidError):
                invalid_token = "Bearer " + str(uuid.uuid4())
                ViewUserInfo(invalid_token)

        fake_user = User(username="test_user", email="test@example.com")
        fake_user.encrypt_pwd("test_password")
        fake_user.expireAt = datetime.datetime.now(
            datetime.timezone.utc
        ) - datetime.timedelta(days=7)

        # Se configura el comportamiento esperado de los mocks
        mock_user.query.filter().first.return_value = fake_user

        # Simula que el usuario no está registrado
        mock_user.query.filter(User.token == valid_access_token).first.return_value = (
            fake_user
        )

        # Verificar que se lance la excepción TokenNotValidError al crear una instancia de ViewUserInfo cuando el token está vencido
        with self.assertRaises(TokenNotValidError):
            valid_token = "Bearer " + valid_access_token
            ViewUserInfo(valid_token)
