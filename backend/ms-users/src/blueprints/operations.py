from flask import jsonify, request, Blueprint
from ..commands.ping import ViewPing
from ..commands.signin import ViewSignIn
from ..commands.user_update import ViewUserUpdate
from ..commands.login import ViewLogInToken
from ..commands.user_info import ViewUserInfo
from ..commands.user_reset import ViewUsersReset

operations_blueprint = Blueprint("operations", __name__)

#Ping
@operations_blueprint.route("/users/ping", methods=["GET"])
def ping():
    return ViewPing().execute()

#Create user
@operations_blueprint.route("/users", methods=["POST"])
def signin():
    data = request.get_json()
    result = ViewSignIn(data).execute()
    response_data = {
        "id": str(result.id),
        "createdAt": result.createdAt.strftime("%Y-%m-%dT%H:%M:%S"),
        "role": str(result.role)
    }

    return jsonify(response_data), 201


@operations_blueprint.route("/users/<string:user_id>", methods=["PATCH"])
def update_user(user_id):
    data = request.get_json()

    ViewUserUpdate(data, user_id=user_id).execute()

    response_data = {"msg": "el usuario ha sido actualizado"}

    return jsonify(response_data), 200

# Authentication with data
@operations_blueprint.route("/users/auth", methods=["POST"])
def login_token():
    data = request.get_json()

    user_authenticated = ViewLogInToken(data).execute()

    response_data = {
        "id": str(user_authenticated.id),
        "token": user_authenticated.token,
        "expireAt": user_authenticated.expireAt.isoformat(),
    }

    return jsonify(response_data), 200

# Data user by token
@operations_blueprint.route("/users/me", methods=["GET"])
def get_user_info():
    bearer_token = request.headers.get("Authorization")

    user_authenticated = ViewUserInfo(bearer_token).execute()

    response_data = {
        "id": str(user_authenticated.id),
        "username": user_authenticated.username,
        "email": user_authenticated.email,
        "fullName": user_authenticated.fullName,
        "dni": user_authenticated.dni,
        "phoneNumber": user_authenticated.phoneNumber,
        "status": user_authenticated.status,
        "role": user_authenticated.role
    }

    return jsonify(response_data), 200

# Reset users
@operations_blueprint.route("/users/reset", methods=["POST"])
def reset():
    ViewUsersReset().execute()

    response_data = {"msg": "Todos los datos fueron eliminados"}

    return jsonify(response_data), 200
