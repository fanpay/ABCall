from ..extensions import db
import datetime

class Incidents(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    user_id = db.Column(db.String(100), nullable=True)
    description = db.Column(db.String(200), nullable=False)
    origin_type = db.Column(db.String(20), nullable=False)
    status = db.Column(db.String(50), nullable=False)
    solution = db.Column(db.String(500), nullable=True)
    creation_date = db.Column(
        db.DateTime, default=datetime.datetime.now(datetime.timezone.utc)
    )
    update_date = db.Column(
        db.DateTime, default=datetime.datetime.now(datetime.timezone.utc)
    )
    solution_agent_id = db.Column(db.String(500), nullable=True)
    solution_date = db.Column(db.DateTime, nullable=True)