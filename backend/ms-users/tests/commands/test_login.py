import unittest
from unittest.mock import MagicMock, patch

from src.commands.login import ViewLogInToken
from src.errors.errors import (
    UserBadRequestError,
    UserNotFoundError,
    BadAuthenticationError,
    UserNotVerifiedError
)
from faker import Faker


class TestViewLogin(unittest.TestCase):
    def setUp(self):
        self.fake = Faker()

    def test_validate_data(self):
        # Datos de ejemplo para la prueba
        valid_data = {
            "username": self.fake.user_name(),
            "password": self.fake.color_name(),
        }
        invalid_data = {
            "dni": self.fake.numerify(text="#######"),
            "fullName": self.fake.name(),
            "phoneNumber": self.fake.phone_number(),
        }

        # Parchea el método validate_data y validate_existing_user para que no levante una excepción
        with patch.object(
            ViewLogInToken, "validate_data", return_value=None
        ), patch.object(
            ViewLogInToken, "validate_existing_user", return_value=None
        ), patch.object(
            ViewLogInToken, "validate_password", return_value=True
        ):

            user_login = ViewLogInToken(valid_data)

        # Verificar que no se lance una excepción con datos válidos
        self.assertIsNone(user_login.validate_data(valid_data))

        # Verificar que se lance una excepción UserBadRequestError con datos inválidos
        with self.assertRaises(UserBadRequestError):
            user_login.validate_data(invalid_data)

    @patch("src.commands.login.User")
    def test_validate_existing_user(self, mock_user):
        # Datos de ejemplo para la pruebas
        username_fake = self.fake.user_name()
        password_fake = self.fake.color_name()
        email_fake = self.fake.email()

        data_json__user_registred = {
            "username": username_fake,
            "password": password_fake,
            "email": email_fake,
        }

        # Se configura el comportamiento esperado de los mocks
        mock_user.query.filter_by().first.return_value = None

        # Simula que el usuario no está registrado
        mock_user.query.filter_by(username=username_fake).first.return_value = None

        with patch.object(ViewLogInToken, "validate_data", return_value=None), \
            patch.object(ViewLogInToken, "validate_password", return_value=None) :

            with self.assertRaises(UserNotFoundError):
                ViewLogInToken(data_json__user_registred)

    @patch("src.commands.login.User")
    def test_validate_password_invalid(self, mock_user):
        # Datos de ejemplo para la prueba
        data_json = {
            "username": self.fake.user_name(),
            "password": self.fake.color_name(),
            "email": self.fake.email(),
        }

        # Simular que la contraseña es inválida
        mock_user.validate_pwd = MagicMock(return_value=False)

        # Crear una instancia de ViewLogInToken y llamar al método validate_password
        with patch.object(ViewLogInToken, "validate_existing_user", return_value=mock_user), \
            patch.object(ViewLogInToken, "validate_password", return_value=None):
            view_login_token = ViewLogInToken(data_json)
            

        # Verificar que el método lance una excepción BadAuthenticationError para una contraseña inválida
        with self.assertRaises(BadAuthenticationError):
            view_login_token.validate_password(mock_user, data_json["password"])
