import unittest
from unittest.mock import MagicMock, patch
from src.errors.errors import BadRequest, NotFound
from src.commands.get_offer import GetOffer


class TestGetOffer(unittest.TestCase):

    def setUp(self):
        # Configura un ID de oferta de ejemplo para usar en las pruebas
        self.offer_id = "123e4567-e89b-12d3-a456-426614174000"

    @patch("src.commands.get_offer.Offer")
    @patch("src.commands.get_offer.OfferSchema")
    def test_execute_offer_found(self, MockOfferSchema, mock_offer):
        # Mock para la oferta encontrada
        mock_offer.query.get.return_value = MagicMock()

        # Crea una instancia de GetOffer con un ID de oferta existente
        get_offer_command = GetOffer(self.offer_id)

        # Ejecuta el método execute
        result = get_offer_command.execute()

        # Verifica que se llamó a Offer.query.get() y OfferSchema().dump()
        mock_offer.query.get.assert_called_once_with(self.offer_id)
        MockOfferSchema().dump.assert_called_once()

    @patch("src.commands.get_offer.Offer")
    def test_execute_offer_not_found(self, mock_offer):
        # Mock para la consulta de la oferta (no se encontró ninguna oferta)
        mock_offer.query.get.return_value = None

        # Crea una instancia de GetOffer con un ID de oferta inexistente
        get_offer_command = GetOffer(self.offer_id)

        # Ejecuta el método execute y verifica que NotFound sea levantado
        with self.assertRaises(NotFound):
            get_offer_command.execute()

    def test_validate_uuid_valid(self):
        # Crea una instancia de GetOffer con un ID de oferta válido
        get_offer_command = GetOffer(self.offer_id)

        # Ejecuta el método validate_uuid
        get_offer_command.validate_uuid(self.offer_id)  # Debería ejecutarse sin errores
