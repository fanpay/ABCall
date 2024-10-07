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
                "userId": inc.userId, 
                "subject": inc.subject, 
                "description": inc.description, 
                "originType": inc.originType, 
                "status": inc.status,
                "creationDate": inc.creationDate.strftime("%Y-%m-%dT%H:%M:%S"),
                "updateDate": inc.updateDate.isoformat()
            }
            for inc in incidents
        ]
        return jsonify(output)

    def post(self):
        data = request.get_json()
        new_incident = Incidents(
            subject=data["subject"], 
            description=data["description"], 
            status=data["status"],
            originType=data["originType"]
        )
        db.session.add(new_incident)
        db.session.commit()
        
        cache.delete('incidents_list')
        return jsonify(
            {"message": "Incident created", "incidentId": new_incident.id}
        )


class IncidentDetail(Resource):
    @cache.cached(timeout=300, key_prefix="incident_%s")
    def get(self, id):
        incident = Incidents.query.get_or_404(id, description="Incident not found")
        return jsonify(
            {
                "id": incident.id,
                "userId": incident.userId,
                "subject": incident.subject,
                "description": incident.description,
                "originType": incident.originType,
                "status": incident.status,
                "solution": incident.solution,
                "creationDate": incident.creationDate.strftime("%Y-%m-%dT%H:%M:%S"),
                "updateDate": incident.updateDate.isoformat(),
                "solutionAgentId": incident.solutionAgentId,
                "solutionDate": incident.solutionDate.isoformat() if incident.solutionDate else None  # None value handling for optional fields in the model and schema
            }
        )

    def put(self, id):
        incident = Incidents.query.get_or_404(id)
        data = request.get_json()
        incident.userId = data.get("userId", incident.userId)
        incident.subject = data.get("subject", incident.subject)
        incident.description = data.get("description", incident.description)
        incident.status = data.get("status", incident.status)
        
        if(data.get("solution")):
            incident.solution = data.get("solution", incident.solution)    
            incident.solutionAgentId = data.get("solutionAgentId", incident.solutionAgentId)
            incident.solutionDate = data.get("solutionDate", incident.solutionDate)
            
        db.session.commit()
        return jsonify({"message": "Incident updated", "incidentId": id})

    def delete(self, id):
        incident = Incidents.query.get_or_404(id)
        db.session.delete(incident)
        db.session.commit()
        return jsonify({"message": "Incident removed", "incidentId": id})
