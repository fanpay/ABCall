from ..extensions import db

class Incidents(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    description = db.Column(db.String(200), nullable=False)
    origin_type = db.Column(db.String(20), nullable=False)
    status = db.Column(db.String(50), nullable=False)
    solution = db.Column(db.String(500), nullable=True)