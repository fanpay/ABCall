class ApiError(Exception):
    code = 422
    description = "Default message"


class SignInBadRequestError(ApiError):
    code = 400
    # El campo username, password o email, no están presentes en la solicitud o email tiene un mal formato
    description = "Los campo username, password o email no están presentes en la solicitud o email tiene un mal formato"


class UserAlreadyExistsError(ApiError):
    code = 412
    # Usuario con el username o el correo ya existe
    description = "Usuario con el username o el correo ya existe"


class UserBadRequestError(ApiError):
    code = 400
    # Se requieren datos para actualizar el usuario
    description = "Se requieren datos para actualizar el usuario"


class UserNotFoundError(ApiError):
    code = 404
    # Usuario no encontrado
    description = "Usuario no encontrado"


class AuthenticationBadRequestError(ApiError):
    code = 400
    # En el caso que alguno de los campos no esté presente en la solicitud
    description = "Alguno de los campos requeridos no está presente en la solicitud"


class BadAuthenticationError(ApiError):
    code = 404
    # En el caso que la contraseña y usuario no coincidan
    description = "La contraseña y usuario no coinciden"


class TokenNotFoundError(ApiError):
    code = 403
    # El token no está en el encabezado de la solicitud.
    description = "El token no está en el encabezado de la solicitud."


class TokenNotValidError(ApiError):
    code = 401
    # El token no es válido o está vencido.
    description = "El token no es válido o está vencido."


class ResetUsersDBError(ApiError):
    code = 500
    #'Error al eliminar registros de usuarios.
    description = "Error al eliminar registros de usuarios"

class ApiErrorCallBack(ApiError):
    code = 500
    #'Error al eliminar registros de usuarios.
    description = "Error al obtener callback de TrueNative API"
    
class TrueNativeTokenNotValidError(ApiError):
    code = 401
    description = "El token TrueNative no es válido o ha sido alterado."
    
class UserNotVerifiedError(ApiError):
    code = 401
    description = "Usuario no verificado."
    