from flask import request
from flask_restful import Resource
from ..models.plan import Plan
from ..extensions import db

class PlanResource(Resource):
    def get(self):
        
        plan = Plan.query.first()
        
        if not plan:
            plan = Plan(plan="Default Plan")
            db.session.add(plan)
            db.session.commit()
            return {
                "message": "Plan created with default values",
                "id": plan.id,
                "plan": plan.plan,
                "creationDate": plan.creationDate.isoformat(),
                "updateDate": plan.updateDate.isoformat()
            }, 201 

        return {
            "id": plan.id,
            "plan": plan.plan,
            "creationDate": plan.creationDate.isoformat(),
            "updateDate": plan.updateDate.isoformat()
        }

    def put(self):
        data = request.get_json()
        plan = Plan.query.first()

        if not plan:
            return {"message": "Plan not found"}, 404

        plan.plan = data.get("plan", plan.plan)

        db.session.commit()

        return {
            "message": "Plan updated",
            "id": plan.id,
            "plan": plan.plan,
            "updateDate": plan.updateDate.isoformat()
        }
