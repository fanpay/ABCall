import unittest
from unittest.mock import patch, MagicMock
from src.commands.list_filter_offers import ListFilterOffers
from src.errors.errors import BadRequest
from src.models.offer import Offer, OfferSchema


class TestListFilterOffers(unittest.TestCase):
    @patch("src.commands.list_filter_offers.Offer")
    @patch("src.commands.list_filter_offers.OfferSchema")
    def test_execute(self, mock_offer_schema, mock_offer):
        # Datos de ejemplo para la prueba
        user_info = {"id": 1}
        query_params = {"post": 123}

        # Mock de la consulta a la base de datos
        mock_query = MagicMock()
        mock_offer.query.filter_by.return_value = mock_query
        mock_query.all.return_value = [
            {"id": 1, "description": "Offer 1"},
            {"id": 2, "description": "Offer 2"},
        ]

        # Mock del esquema de la oferta
        mock_offer_schema_instance = mock_offer_schema.return_value
        mock_offer_schema_instance.dump.return_value = [
            {"id": 1, "description": "Offer 1"},
            {"id": 2, "description": "Offer 2"},
        ]

        # Crear instancia de ListFilterOffers y llamar a validate_params
        list_filter_command = ListFilterOffers(user_info, query_params)
        list_filter_command.validate_params(
            query_params
        )  # Llamar a validate_params aquí

        # Ejecutar el método execute
        result = list_filter_command.execute()

        # Verificar que se haya llamado a la consulta con los filtros correctos
        mock_offer.query.filter_by.assert_called_once_with(postId=123)

        # Verificar que se haya llamado a la función 'all' en la consulta
        mock_query.all.assert_called_once()

        # Verificar que se haya llamado al método 'dump' del esquema de oferta con la lista de ofertas
        mock_offer_schema_instance.dump.assert_called_once_with(
            [{"id": 1, "description": "Offer 1"}, {"id": 2, "description": "Offer 2"}]
        )

        # Verificar que el resultado es la lista de ofertas obtenida del esquema
        self.assertEqual(
            result,
            [{"id": 1, "description": "Offer 1"}, {"id": 2, "description": "Offer 2"}],
        )
