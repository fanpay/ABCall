from flask import Flask, jsonify, request, Blueprint, send_file
from ..commands.generate_report_command import Generatereport

reports_blueprint = Blueprint("reports", __name__)


@reports_blueprint.route("/dash-reports/reports", methods=["GET"])
def ping():
    
    zip_buffer = Generatereport().execute()
     # Enviar el archivo protegido
    return send_file(zip_buffer, mimetype='application/zip', as_attachment=True, download_name='reporte.zip')
