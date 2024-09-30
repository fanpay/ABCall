import os
import sys
import pytest

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..', 'src')))

from src.app import app
from src.extensions import db
from src.models.incidents import Incidents

@pytest.fixture
def client():
    app.config.from_object('src.config.TestConfig')
    with app.test_client() as client:
        with app.app_context():
            db.create_all()
        yield client
        db.drop_all()

def test_get_incidents(client):
    response = client.get('/incidents')
    assert response.status_code == 200

def test_post_incident(client):
    response = client.post('/incidents', json={'description': 'Test incident', 'origin_type': 'web', 'status': 'Open'})
    assert response.status_code == 200
    assert b"Incident created" in response.data

def test_get_incident_not_found(client):
    # Intentar obtener un incidente inexistente
    response = client.get('/incidents/9999')
    assert response.status_code == 404


def test_delete_incident_not_found(client):
    # Intentar eliminar un incidente inexistente
    response = client.delete('/incidents/9999')
    assert response.status_code == 404

