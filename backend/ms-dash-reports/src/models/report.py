# /models/offer.py
import uuid
import datetime
from sqlalchemy.dialects.postgresql import UUID
from marshmallow import Schema, fields
from ..extensions import db


class Report(db.Model):
    id = db.Column(UUID(as_uuid=True), primary_key=True, default=uuid.uuid4)
    clientId = db.Column(db.String)
    status = db.Column(db.String(length=140))
    type = db.Column(db.String(length=140))
    creationDate = db.Column(
        db.DateTime, default=datetime.datetime.now(datetime.timezone.utc)
    )
    generationDate = db.Column(db.DateTime)

# Especificar los campos que estarán presentes al serializar el objeto como JSON.
class ReportSchema(Schema):
    class Meta:
        model = Report
        load_instance = True

    id = fields.Str()
    clientId = fields.Str()
    status = fields.Str()
    type = fields.Str()
    creationDate = fields.DateTime()
    generationDate = fields.DateTime()
