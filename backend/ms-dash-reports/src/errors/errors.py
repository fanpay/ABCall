class ApiError(Exception):
    code = 422
    description = "Default message"


class BadRequest(ApiError):
    code = 400
    description = ""


class PreconditionFailed(ApiError):
    code = 412
    description = ""


class Forbidden(ApiError):
    code = 403
    description = ""


class ResetDBError(ApiError):
    code = 424
    description = "Error al eliminar los registros de las ofertas."


class NotFound(ApiError):
    code = 404
    description = ""
