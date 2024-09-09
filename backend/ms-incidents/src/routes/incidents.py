from flask import request, jsonify
from flask_restful import Resource
from ..models.incidents import Incidents
from ..extensions import db, cache


class IncidentsList(Resource):
    @cache.cached(timeout=30)
    def get(self):
        incidents = Incidents.query.all()
        output = [
            {"id": inc.id, "description": inc.description, "status": inc.status}
            for inc in incidents
        ]
        return jsonify(output)

    def post(self):
        data = request.get_json()
        new_incident = Incidents(
            description=data["description"], 
            status=data["status"],
            origin_type=data["origin_type"]
        )
        db.session.add(new_incident)
        db.session.commit()
        
        cache.delete('incidents_list')
        return jsonify(
            {"message": "Incident created", "id": new_incident.id}
        )


class IncidentDetail(Resource):
    @cache.cached(timeout=300, key_prefix="incident_%s")
    def get(self, id):
        incident = Incidents.query.get_or_404(id)
        return jsonify(
            {
                "id": incident.id,
                "description": incident.description,
                "origin_type": incident.origin_type,
                "status": incident.status,
                "solution": incident.solution
            }
        )

    def put(self, id):
        incident = Incidents.query.get_or_404(id)
        data = request.get_json()
        incident.description = data.get("description", incident.description)
        incident.status = data.get("status", incident.status)
        incident.solution = data.get("solution", incident.solution)
        db.session.commit()
        return jsonify({"message": "Incident updated"})

    def delete(self, id):
        incident = Incidents.query.get_or_404(id)
        db.session.delete(incident)
        db.session.commit()
        return jsonify({"message": "Incident removed"})
