from dotenv import load_dotenv

loaded = load_dotenv()

import requests

from flask import Flask, jsonify, request

from .blueprints.ping import ping_blueprint
from .blueprints.reports import reports_blueprint
from .errors.errors import ApiError
from .extensions import db

import os
from .errors.errors import Forbidden

app = Flask(__name__)

app.config["SQLALCHEMY_DATABASE_URI"] = (
    f'postgresql://{os.environ["DB_USER"]}:{os.environ["DB_PASSWORD"]}@{os.environ["DB_HOST"]}:{os.environ["DB_PORT"]}/{os.environ["DB_NAME"]}'
)
db.init_app(app)

# Bd config
with app.app_context():
    db.create_all()

# Registers
app.register_blueprint(ping_blueprint)
app.register_blueprint(reports_blueprint)


# Middlewares
@app.errorhandler(ApiError)
def handle_exception(err):
    response = {"msg": err.description}
    return jsonify(response), err.code


@app.before_request
def load_user():
    if (
        request.blueprint != ping_blueprint.name
        and request.blueprint != reports_blueprint.name
    ):
        authorization_header = request.headers.get("Authorization")

        if authorization_header:
            try:
                token = authorization_header.split(" ")[1]
                user_info = validate_bearer_token(token)
                request.user_info = user_info

            except Exception as e:
                return jsonify({"error": str(e)}), 401
        else:
            raise Forbidden


def validate_bearer_token(token):
    headers = {"Authorization": f"Bearer {token}"}
    response = requests.get(f'{os.environ["USERS_PATH"]}/users/me', headers=headers)

    if response.status_code == 200:
        return response.json()
    else:
        raise Exception("Invalid Bearer Token")


if __name__ == "__main__":
    app.run(debug=True)
