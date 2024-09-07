import unittest
from unittest.mock import patch, MagicMock
from src.commands.create import CreateOffer
from src.errors.errors import BadRequest, PreconditionFailed


class TestCreateOffer(unittest.TestCase):

    def test_validate_data(self):
        # Datos de ejemplo para la prueba
        valid_data = {
            "postId": 123,
            "description": "Test description",
            "size": "LARGE",
            "fragile": True,
            "offer": 100,
        }

        invalid_data_missing_keys = {
            "postId": 123,
            "size": "LARGE",
            "fragile": True,
        }  # Faltan keys
        invalid_data_invalid_size = {
            "postId": 123,
            "description": "Test description",
            "size": "INVALID",
            "fragile": True,
            "offer": 100,
        }  # Tamaño no válido
        invalid_data_negative_offer = {
            "postId": 123,
            "description": "Test description",
            "size": "LARGE",
            "fragile": True,
            "offer": -100,
        }  # Oferta negativa

        # Crear instancias de CreateOffer para cada caso
        create_offer_command_valid = CreateOffer({}, valid_data)

        with self.assertRaises(BadRequest):
            create_offer_command_valid.validate_data(invalid_data_missing_keys)

        with self.assertRaises(PreconditionFailed):
            create_offer_command_valid.validate_data(invalid_data_invalid_size)

        with self.assertRaises(PreconditionFailed):
            create_offer_command_valid.validate_data(invalid_data_negative_offer)


if __name__ == "__main__":
    unittest.main()
