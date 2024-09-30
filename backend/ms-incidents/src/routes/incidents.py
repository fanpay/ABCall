from flask import request, jsonify
from flask_restful import Resource
from ..models.incidents import Incidents
from ..extensions import db, cache


class IncidentsList(Resource):
    @cache.cached(timeout=30)
    def get(self):
        incidents = Incidents.query.all()
        output = [
            {
                "id": inc.id, 
                "user_id": inc.user_id, 
                "description": inc.description, 
                "origin_type": inc.origin_type, 
                "status": inc.status,
                "creation_date": inc.creation_date.strftime("%Y-%m-%dT%H:%M:%S"),
                "update_date": inc.update_date.isoformat()
            }
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
            {"message": "Incident created", "incident_id": new_incident.id}
        )


class IncidentDetail(Resource):
    @cache.cached(timeout=300, key_prefix="incident_%s")
    def get(self, id):
        incident = Incidents.query.get_or_404(id)
        return jsonify(
            {
                "id": incident.id,
                "user_id": incident.user_id,
                "description": incident.description,
                "origin_type": incident.origin_type,
                "status": incident.status,
                "solution": incident.solution,
                "creation_date": incident.creation_date.strftime("%Y-%m-%dT%H:%M:%S"),
                "update_date": incident.update_date.isoformat(),
                "solution_agent_id": incident.solution_agent_id,
                "solution_date": incident.solution_date.isoformat() if incident.solution_date else None  # None value handling for optional fields in the model and schema
            }
        )

    def put(self, id):
        incident = Incidents.query.get_or_404(id)
        data = request.get_json()
        incident.user_id = data.get("user_id", incident.user_id)
        incident.description = data.get("description", incident.description)
        incident.status = data.get("status", incident.status)
        
        if(data.get("solution")):
            incident.solution = data.get("solution", incident.solution)    
            incident.solution_agent_id = data.get("solution_agent_id", incident.solution_agent_id)
            incident.solution_date = data.get("solution_date", incident.solution_date)
            
        db.session.commit()
        return jsonify({"message": "Incident updated", "incident_id": id})

    def delete(self, id):
        incident = Incidents.query.get_or_404(id)
        db.session.delete(incident)
        db.session.commit()
        return jsonify({"message": "Incident removed", "incident_id": id})
