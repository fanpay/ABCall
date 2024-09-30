import datetime
import hashlib
import os
import logging
import requests

from .base_command import BaseCommannd
from ..errors.errors import UserBadRequestError, UserNotFoundError, TrueNativeTokenNotValidError
from ..models.user import User
from ..extensions import db

logging.basicConfig(level=logging.INFO)

class ViewUserUpdate(BaseCommannd):
    def __init__(self, data_json, user_id):

        self.validate_data(data_json)

        self.user_by_id = self.validate_existing_user(user_id)

        self.data = data_json
        self.fields_to_update = ["status", "dni", "fullName", "phoneNumber"]

    def execute(self):
        for field in self.fields_to_update:
            if field in self.data:
                setattr(self.user_by_id, field, self.data[field])

        self.user_by_id.updateAt = datetime.datetime.now(datetime.timezone.utc)
        db.session.commit()

        self.send_email()

        return self.user_by_id

    def validate_data(self, data):
        if not data or not any(
            field in data for field in ["status", "dni", "fullName", "phoneNumber"]
        ):
            raise UserBadRequestError


        if self.validate_token_webhook(data):
            raise TrueNativeTokenNotValidError


    def validate_existing_user(self, user_id):
        user_by_id = User.query.filter(User.id == user_id).first()

        if not user_by_id:
            raise UserNotFoundError(f"User with id {user_id} not found")

        return user_by_id


    def validate_token_webhook(self, data):
        logging.info(f"[validate_token_webhook DATA ->{data}")

        token = f'{os.environ["SECRET_TOKEN"]}:{data["RUV"]}:{data["score"]}'
        sha_token = hashlib.sha256(token.encode()).hexdigest()
        assert sha_token == data["verifyToken"]


    def send_email(self):
        logging.info("SENDING EMAIL")
        url = 'https://us-central1-uandes-native-413514.cloudfunctions.net/send-email-http'

        # Define the JSON data to be sent in the request
        json_data = {
            "to": self.user_by_id.email,
            "body" : f'Tu usuario ha sido validado. El estado actual es {self.data["status"]}',
            "subject": f'Tu usuario ha sido {self.data["status"]}'
        }

        # Make the HTTP POST request
        response = requests.post(url, json=json_data)

        # Check the response status
        if response.status_code == 200:
            print("Email sent successfully")
        else:
            print("Failed to send email")


