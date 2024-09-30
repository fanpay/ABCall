import re
import requests
import os
import logging
import uuid

from retrying import retry

from .base_command import BaseCommannd
from ..errors.errors import SignInBadRequestError, UserAlreadyExistsError, ApiErrorCallBack
from ..models.user import User
from ..extensions import db

logging.basicConfig(level=logging.INFO)

class ViewSignIn(BaseCommannd):
    def __init__(self, data_json):

        self.validate_data(data_json)

        self.validate_existing_user(data_json["username"], data_json["email"])

        self.username = data_json["username"]
        self.password = data_json["password"]
        self.email = data_json["email"]
        self.dni = data_json.get("dni", "")
        self.fullName = (data_json.get("fullName", ""),)
        self.phoneNumber = data_json.get("phoneNumber", "")

    def execute(self):
        new_user = User(
            username=self.username,
            email=self.email,
            dni=self.dni,
            fullName=self.fullName,
            phoneNumber=self.phoneNumber,
        )

        new_user.encrypt_pwd(self.password)

        db.session.add(new_user)
        db.session.commit()

        return new_user

    def validate_data(self, data):
        if not all(key in data for key in ("username", "password", "email")) or (
            not self.check_email(data["email"])
        ):
            raise SignInBadRequestError

    def validate_existing_user(self, username, email):
        existing_username = User.query.filter_by(username=username).first()
        existing_email = User.query.filter_by(email=email).first()

        if existing_username or existing_email:
            raise UserAlreadyExistsError

    def check_email(self, texto):
        # Expresión regular para validar direcciones de correo electrónico
        pattern = r"^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$"

        # re.match para verificar si el texto coincide con el patrón
        if re.match(pattern, texto):
            return True
        else:
            return False

