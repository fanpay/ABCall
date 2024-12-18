from flask import request
from flask_restful import Resource
from datetime import datetime, timedelta
import pytz
from ..models.incidents import Incidents
from ..extensions import db, cache
from ..services.cache_service import clear_incident_cache


class IncidentsList(Resource):
    @cache.cached(timeout=30)
    def get(self):
        user_id = request.args.get('userId')
        
        if user_id:
            incidents = Incidents.query.filter_by(userId=user_id).all()
        else:
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
                "updateDate": inc.updateDate.isoformat(),
                "solution": inc.solution,
                "solutionAgentId": inc.solutionAgentId,
                "solutionDate": inc.solutionDate.isoformat() if inc.solutionDate else None 
            }
            for inc in incidents
        ]
        return output

    def post(self):
        try:
            data = request.get_json()
            
            print(f"Data received: {data}") 
            
            required_fields = ['userId', 'subject', 'description', 'originType']
            missing_fields = [field for field in required_fields if field not in data]

            print(f"Missing fields: {', '.join(missing_fields)}") 

            if missing_fields:
                # Devuelve un diccionario en formato JSON y el código de estado 400
                return {"message": f"Missing fields: {', '.join(missing_fields)}"}, 400        
        
            new_incident = Incidents(
                userId=data["userId"],
                subject=data["subject"], 
                description=data["description"], 
                status="OPEN",
                originType=data["originType"]
            )
            db.session.add(new_incident)
            db.session.commit()
            
            cache.clear()
            return {
                "message": "Incident created", 
                "incidentId": new_incident.id
            }, 201
        except Exception as e:
            return {"error": str(e)}, 500


class IncidentDetail(Resource):
    @cache.cached(timeout=30, key_prefix="incident_%s")
    def get(self, id):
        incident = Incidents.query.get_or_404(id, description="Incident not found")
        return {
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
        
        clear_incident_cache(incident.id)
        
        return {"message": "Incident updated", "incidentId": id}

    def delete(self, id):
        incident = Incidents.query.get_or_404(id)
        db.session.delete(incident)
        db.session.commit()
        return {"message": "Incident removed", "incidentId": id}

class IncidentsMetrics(Resource):
    def get(self):
        current_date = datetime.now(pytz.utc)
        thirty_days_ago = current_date - timedelta(days=30)

        open_incidents_count = Incidents.query.filter(
            Incidents.creationDate >= thirty_days_ago,
            Incidents.status == "OPEN"
        ).count()

        closed_incidents_count = Incidents.query.filter(
            Incidents.creationDate >= thirty_days_ago,
            Incidents.status == "CLOSED"
        ).count()

        return {
            "open_incidents_count": open_incidents_count,
            "closed_incidents_count": closed_incidents_count
        }
    
class IncidentsInRange(Resource):
    def get(self):
        
        start_date_str = request.args.get('startDate')
        end_date_str = request.args.get('endDate')

        if not start_date_str or not end_date_str:
            return {"message": "Both startDate and endDate are required."}, 400
        
        try:
            # Convertir las fechas en formato ISO a objetos datetime
            start_date = datetime.fromisoformat(start_date_str)
            end_date = datetime.fromisoformat(end_date_str)
        except ValueError:
            return {"message": "Invalid date format. Use ISO format (YYYY-MM-DDTHH:MM:SS)."}, 400
        
        # Filtrar las incidencias dentro del rango de fechas
        incidents = Incidents.query.filter(
            Incidents.creationDate >= start_date,
            Incidents.creationDate <= end_date
        ).all()

        output = [
            {
                "id": inc.id,
                "userId": inc.userId,
                "subject": inc.subject,
                "description": inc.description,
                "originType": inc.originType,
                "status": inc.status,
                "creationDate": inc.creationDate.strftime("%Y-%m-%dT%H:%M:%S"),
                "updateDate": inc.updateDate.isoformat(),
                "solution": inc.solution,
                "solutionAgentId": inc.solutionAgentId,
                "solutionDate": inc.solutionDate.isoformat() if inc.solutionDate else None
            }
            for inc in incidents
        ]
        
        return output
