import unittest
from unittest.mock import patch
from src.commands.reset import ResetOffers
from src.errors.errors import ResetDBError
from faker import Faker


class TestOfferReset(unittest.TestCase):

    def setUp(self):
        self.fake = Faker()

    @patch("src.commands.reset.Offer")
    @patch("src.commands.reset.db.session")
    def test_execute(self, mock_session, mock_offer):
        # Simula que la eliminación se realizó correctamente
        mock_offer.query.delete.return_value = None

        # Simula que la sesión se confirmó correctamente
        mock_session.commit.return_value = None

        # Crear una instancia de ResetOffer y llamar al método execute
        reset = ResetOffers()
        result = reset.execute()

        # Verificar que la eliminación de usuarios se realizó correctamente
        self.assertEqual(result, {"msg": "Todos los datos fueron eliminados"})

    @patch("src.commands.reset.Offer")
    @patch("src.commands.reset.db.session")
    def test_execute_rollback(self, mock_session, mock_offer):
        # Simula un error al eliminar usuarios
        mock_offer.query.delete.side_effect = Exception()

        # Simula que el rollback se realizó correctamente
        mock_session.rollback.return_value = None

        # Crear una instancia de ResetOffer y llamar al método execute
        reset = ResetOffers()

        # Verificar que se lance una excepción ResetDBError cuando ocurre un error
        with self.assertRaises(ResetDBError):
            reset.execute()
