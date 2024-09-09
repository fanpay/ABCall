from flask import Flask, jsonify, request, Blueprint

ping_blueprint = Blueprint("ping", __name__)


@ping_blueprint.route("/dash-reports/ping", methods=["GET"])
def ping():
    return "pong"
