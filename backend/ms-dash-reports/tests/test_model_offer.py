import unittest
from src.models.offer import Offer, OfferSchema
from datetime import datetime
from marshmallow import ValidationError


class TestOfferModel(unittest.TestCase):

    def test_offer_model(self):
        # Crear una instancia de Offer
        offer = Offer(
            postId="1",
            userId="user123",
            description="This is an offer description",
            size="Large",
            fragile=False,
            offer=100,
            createdAt=datetime.utcnow(),
        )

        # Comprobar que los atributos se asignaron correctamente
        self.assertEqual(offer.postId, "1")
        self.assertEqual(offer.userId, "user123")
        self.assertEqual(offer.description, "This is an offer description")
        self.assertEqual(offer.size, "Large")
        self.assertEqual(offer.fragile, False)
        self.assertEqual(offer.offer, 100)
