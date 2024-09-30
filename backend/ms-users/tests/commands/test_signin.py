import unittest
import os
from unittest.mock import MagicMock, patch

from src.commands.signin import ViewSignIn
from src.errors.errors import UserAlreadyExistsError, SignInBadRequestError, ApiErrorCallBack
from faker import Faker


class TestViewSignIn(unittest.TestCase):
    def setUp(self):
        os.environ['SECRET_TOKEN'] = 'test'
        os.environ['TRUENATIVE_PATH'] = 'http://truenative_path'
        os.environ['USERS_PATH'] = 'http://user_path'
        self.fake = Faker()

    def test_validate_data(self):
        # Datos de ejemplo para la prueba
        valid_data = {
            "username": self.fake.user_name(),
            "password": self.fake.color_name(),
            "email": self.fake.email(),
        }
        invalid_data = {
            "dni": self.fake.numerify(text="#######"),
            "fullName": self.fake.name(),
            "phoneNumber": self.fake.phone_number(),
            "status": "POR_VERIFICAR",
        }

        # Parchea el método validate_data y validate_existing_user para que no levante una excepción
        with patch.object(ViewSignIn, "validate_data", return_value=None), patch.object(
            ViewSignIn, "validate_existing_user", return_value=None
        ):
            # Crea una instancia de ViewSignIn
            view_signin = ViewSignIn(valid_data)

        # Verificar que no se lance una excepción con datos válidos
        self.assertIsNone(view_signin.validate_data(valid_data))

        # Verificar que se lance una excepción SignInBadRequestError con datos inválidos
        with self.assertRaises(SignInBadRequestError):
            view_signin.validate_data(invalid_data)

    def test_check_email(self):
        # Datos de ejemplo para la prueba
        email_fake = self.fake.email()
        valid_data = {
            "username": self.fake.user_name(),
            "password": self.fake.color_name(),
            "email": email_fake,
        }

        # Parchea el método validate_data y validate_existing_user para que no levante una excepción
        with patch.object(ViewSignIn, "validate_data", return_value=None), patch.object(
            ViewSignIn, "validate_existing_user", return_value=None
        ):
            # Crea una instancia de ViewSignIn
            view_signin = ViewSignIn(valid_data)

        self.assertTrue(view_signin.check_email(email_fake))
        self.assertFalse(view_signin.check_email(self.fake.job()))

    @patch("src.models.user.User")
    @patch("src.extensions.db.session")
    @patch('src.commands.signin.retry', side_effect=lambda *args, **kwargs: lambda f: f)
    @patch('requests.get')
    def test_execute_mock(self, mock_session, mock_user, mock_requests_get, mock_retry):
        # Datos de ejemplo para la pruebas
        username_fake = self.fake.user_name()
        password_fake = self.fake.color_name()
        email_fake = self.fake.email()

        data_json = {
            "username": username_fake,
            "password": password_fake,
            "email": email_fake,
        }

        # Se configura el comportamiento esperado de los mocks
        mock_user.query.filter_by().first.return_value = None

        # Simula que el usuario no está registrado
        mock_user.query.filter_by(username=username_fake).first.return_value = None

        # Simula que el email no está registrado
        mock_user.query.filter_by(email=email_fake).first.return_value = None

        # Simula que el usuario se agrega correctamente
        mock_session.add.return_value = None

        # Parchea el método validate_data y validate_existing_user para que no levante una excepción
        with patch.object(ViewSignIn, "validate_data", return_value=None), \
            patch.object(ViewSignIn, "validate_existing_user", return_value=None):
           
            # Crea una instancia de ViewSignIn
            view_signin = ViewSignIn(data_json)

        mock_requests_get.return_value.status_code = 422
        with self.assertRaises(ApiErrorCallBack):
            result = view_signin.execute()

        # Verificar que se llamó a User.query.filter_by() con los valores esperados
        mock_user.query.filter_by.assert_any_call(username=username_fake)
        mock_user.query.filter_by.assert_any_call(email=email_fake)

        # Verificar que se llamó a db.session.add() y db.session.commit() con los valores esperados
        #mock_session.add.assert_called_once_with(result)
        #mock_session.commit.assert_called_once()

    # @patch('src.models.user.User')
    @patch("src.commands.signin.User")
    def test_validate_existing_user_mock(self, mock_user):
        # Datos de ejemplo para la pruebas
        username_fake = self.fake.user_name()
        password_fake = self.fake.color_name()
        email_fake = self.fake.email()

        data_json = {
            "username": username_fake,
            "password": password_fake,
            "email": email_fake,
        }

        # Configurar el comportamiento esperado de los mocks
        # mock_user.query.filter_by().first.return_value = MagicMock() # Simula que el usuario existe
        # mock_user.query.filter_by(username=username_fake).first.return_value = MagicMock()  # Simula que el usuario existe
        # mock_user.query.filter_by(email=email_fake).first.return_value = None  # Simula que el email no está registrado

        mock_query = MagicMock()
        mock_user.query = MagicMock(return_value=mock_query)
        mock_query.filter_by.return_value.first.return_value = MagicMock()

        # Verificar que se lance la excepción UserAlreadyExistsError al crear una instancia de ViewSignIn
        with self.assertRaises(UserAlreadyExistsError):
            ViewSignIn(data_json)
