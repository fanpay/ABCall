# /models/user.py
import uuid
import bcrypt
import datetime
from sqlalchemy.dialects.postgresql import UUID
from marshmallow import Schema, fields
from ..extensions import db


# Extender la clase Model proporcionada
class User(db.Model):
    id = db.Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    username = db.Column(db.String, unique=True, nullable=False)
    email = db.Column(db.String, unique=True, nullable=False)
    phoneNumber = db.Column(db.String, nullable=True)
    dni = db.Column(db.String, nullable=True)
    fullName = db.Column(db.String, nullable=True)
    password = db.Column(db.LargeBinary, nullable=False)
    salt = db.Column(db.LargeBinary, nullable=False)
    token = db.Column(db.String, nullable=True)
    status = db.Column(
        db.String, nullable=False, default="POR_VERIFICAR"
    )  # POR_VERIFICAR, NO_VERIFICADO, VERIFICADO
    expireAt = db.Column(db.DateTime)
    createdAt = db.Column(
        db.DateTime, default=datetime.datetime.now(datetime.timezone.utc)
    )
    updateAt = db.Column(
        db.DateTime, default=datetime.datetime.now(datetime.timezone.utc)
    )

    def encrypt_pwd(self, password):
        """
        Hashea la contraseña y almacena la sal y la contraseña hasheada en el objeto User
        """
        self.salt = bcrypt.gensalt()
        self.password = bcrypt.hashpw(password.encode("utf-8"), self.salt)

    def validate_pwd(self, password):
        """
        Verifica la contraseña hasheada con la sal del objeto User
        """
        hashed_password = bcrypt.hashpw(password.encode("utf-8"), self.salt)
        return hashed_password == self.password


class UserSchema(Schema):
    class Meta:
        model = User
        include_relationships = False
        load_instance = True
        include_fk = True

    id = fields.Number()
    username = fields.String()
    email = fields.String()
    dni = fields.String()
    fullName = fields.String()
    password = fields.String()
    salt = fields.String()
    token = fields.String()
    status = fields.String()
    expireAt = fields.DateTime()
    createdAt = fields.DateTime()
    updateAt = fields.DateTime()
