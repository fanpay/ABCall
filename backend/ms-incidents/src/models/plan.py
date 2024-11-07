from ..extensions import db
import datetime

class Plan(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    plan = db.Column(db.String(100), nullable=True)
    creationDate = db.Column(
        db.DateTime, default=datetime.datetime.now(datetime.timezone.utc)
    )
    updateDate = db.Column(
        db.DateTime, default=datetime.datetime.now(datetime.timezone.utc)
    )