import datetime
import uuid

from .base_command import BaseCommannd
from ..errors.errors import (
    UserBadRequestError,
    UserNotFoundError,
    BadAuthenticationError,
    UserNotVerifiedError
)
from ..models.user import User
from ..extensions import db


class ViewLogInToken(BaseCommannd):
    def __init__(self, data_json):
        self.validate_data(data_json)

        self.user_by_username = self.validate_existing_user(str(data_json["username"]))

        self.validate_password(self.user_by_username, str(data_json["password"]))

    def execute(self):
        # Generar un nuevo token de autenticación
        access_token = str(uuid.uuid4())

        # Actualizar el token de autenticación del usuario en la base de datos y da vigencia de 7 días al token
        self.user_by_username.token = access_token
        self.user_by_username.updateAt = datetime.datetime.now(datetime.timezone.utc)
        self.user_by_username.expireAt = (
            self.user_by_username.updateAt + datetime.timedelta(days=7)
        )
        db.session.commit()

        return self.user_by_username

    def validate_data(self, data):
        if not all(key in data for key in ("username", "password")):
            raise UserBadRequestError

    def validate_existing_user(self, username):
        user_by_username = User.query.filter_by(username=username).first()

        if not user_by_username:
            raise UserNotFoundError(f"User with username {username} not found")
        
        if user_by_username.status != "VERIFICADO":
            raise UserNotVerifiedError(f"User with username {username} is not verified")

        return user_by_username

    def validate_password(self, user_by_username, password):
        if not user_by_username.validate_pwd(password):
            raise BadAuthenticationError
