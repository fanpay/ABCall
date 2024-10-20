import datetime

import pytz

from .base_command import BaseCommannd
from ..errors.errors import TokenNotFoundError, TokenNotValidError
from ..models.user import User
from ..extensions import db


class ViewUserInfo(BaseCommannd):
    def __init__(self, bearer_token):

        self.validate_token(bearer_token)

        # Parsea el token de portador (en este caso, asumiendo que es solo el token sin 'Bearer ')
        token = bearer_token.split(" ")[1]

        self.user_by_token = self.validate_existing_user(token)

    def execute(self):
        return self.user_by_token

    def validate_token(self, bearer_token):
        if not bearer_token:
            raise TokenNotFoundError

    def validate_existing_user(self, token):
        user_by_token = User.query.filter(User.token == token).first()

        if not user_by_token:
            raise TokenNotValidError

        # Se converte este dato a un datetime con zona horaria UTC y evitamos problemas en la verificación
        expire_at_utc = user_by_token.expireAt.astimezone(pytz.utc)

        # Verifica si el token ha expirado
        if expire_at_utc < datetime.datetime.now(datetime.timezone.utc):
            raise TokenNotValidError

        return user_by_token
