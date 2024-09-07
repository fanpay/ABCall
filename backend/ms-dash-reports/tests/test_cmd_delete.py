import unittest
from unittest.mock import patch, MagicMock
from src.commands.delete_offer import DeleteOffer
from src.models.offer import Offer
from src.extensions import db
from src.errors.errors import BadRequest, NotFound


class TestDeleteOffer(unittest.TestCase):
    def test_validate_uuid(self):
        # Datos de ejemplo para la prueba
        valid_id = "123e4567-e89b-12d3-a456-426614174000"
        invalid_id = "invalid_id"

        # Crear instancias de DeleteOffer para cada caso
        delete_offer_command_valid = DeleteOffer(valid_id)

        with self.assertRaises(BadRequest):
            delete_offer_command_valid.validate_uuid(invalid_id)
