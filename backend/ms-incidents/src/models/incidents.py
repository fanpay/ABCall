from ..extensions import db
import datetime

class Incidents(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    userId = db.Column(db.String(100), nullable=True)
    subject = db.Column(db.String(100), nullable=True)
    description = db.Column(db.String(1000), nullable=False)
    originType = db.Column(db.String(20), nullable=False)
    status = db.Column(db.String(50), nullable=False)
    solution = db.Column(db.String(1000), nullable=True)
    creationDate = db.Column(
        db.DateTime, default=datetime.datetime.now(datetime.timezone.utc)
    )
    updateDate = db.Column(
        db.DateTime, default=datetime.datetime.now(datetime.timezone.utc)
    )
    solutionAgentId = db.Column(db.String(50), nullable=True)
    solutionDate = db.Column(db.DateTime, nullable=True)
