import unittest
from unittest.mock import patch
from src.commands.user_reset import ViewUsersReset
from src.errors.errors import ResetUsersDBError
from faker import Faker


class TestUserReset(unittest.TestCase):

    def setUp(self):
        self.fake = Faker()

    @patch("src.commands.user_reset.User")
    @patch("src.commands.user_reset.db.session")
    def test_execute(self, mock_session, mock_user):
        # Simula que la eliminación se realizó correctamente
        mock_user.query.delete.return_value = None

        # Simula que la sesión se confirmó correctamente
        mock_session.commit.return_value = None

        # Crear una instancia de ViewUsersReset y llamar al método execute
        view_users_reset = ViewUsersReset()
        result = view_users_reset.execute()

        # Verificar que la eliminación de usuarios se realizó correctamente
        self.assertEqual(result, {"msg": "Todos los datos fueron eliminados"})

    @patch("src.commands.user_reset.User")
    @patch("src.commands.user_reset.db.session")
    def test_execute_rollback(self, mock_session, mock_user):
        # Simula un error al eliminar usuarios
        mock_user.query.delete.side_effect = Exception()

        # Simula que el rollback se realizó correctamente
        mock_session.rollback.return_value = None

        # Crear una instancia de ViewUsersReset y llamar al método execute
        view_users_reset = ViewUsersReset()

        # Verificar que se lance una excepción ResetUsersDBError cuando ocurre un error
        with self.assertRaises(ResetUsersDBError):
            view_users_reset.execute()
