from .base_command import BaseCommannd
from ..errors.errors import ResetUsersDBError
from ..models.user import User
from ..extensions import db


class ViewUsersReset(BaseCommannd):
    def execute(self):
        try:
            User.query.delete()
            db.session.commit()
            return {"msg": "Todos los datos fueron eliminados"}
        except Exception:
            db.session.rollback()
            raise ResetUsersDBError
