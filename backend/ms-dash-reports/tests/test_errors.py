import unittest
from src.errors.errors import (
    ApiError,
    BadRequest,
    PreconditionFailed,
    Forbidden,
    ResetDBError,
    NotFound,
)


class TestApiErrors(unittest.TestCase):

    def test_api_error_attributes(self):
        # Verificar los atributos de la clase base ApiError
        api_error = ApiError()
        self.assertEqual(api_error.code, 422)
        self.assertEqual(api_error.description, "Default message")

    def test_bad_request_error_attributes(self):
        # Verificar los atributos de la clase BadRequest
        bad_request_error = BadRequest()
        self.assertEqual(bad_request_error.code, 400)
        self.assertEqual(bad_request_error.description, "")

    def test_precondition_failed_error_attributes(self):
        # Verificar los atributos de la clase PreconditionFailed
        precondition_failed_error = PreconditionFailed()
        self.assertEqual(precondition_failed_error.code, 412)
        self.assertEqual(precondition_failed_error.description, "")

    def test_forbidden_error_attributes(self):
        # Verificar los atributos de la clase Forbidden
        forbidden_error = Forbidden()
        self.assertEqual(forbidden_error.code, 403)
        self.assertEqual(forbidden_error.description, "")

    def test_reset_db_error_attributes(self):
        # Verificar los atributos de la clase ResetDBError
        reset_db_error = ResetDBError()
        self.assertEqual(reset_db_error.code, 424)
        self.assertEqual(
            reset_db_error.description,
            "Error al eliminar los registros de las ofertas.",
        )

    def test_not_found_error_attributes(self):
        # Verificar los atributos de la clase NotFound
        not_found_error = NotFound()
        self.assertEqual(not_found_error.code, 404)
        self.assertEqual(not_found_error.description, "")
