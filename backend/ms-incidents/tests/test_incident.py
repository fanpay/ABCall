import os
import sys
import pytest
from unittest.mock import patch

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..', 'src')))

from src.app import create_app

# Mock de las variables de entorno
@pytest.fixture(autouse=True)
def mock_env_variables(monkeypatch):
    monkeypatch.setenv('DB_USER', 'test_user')
    monkeypatch.setenv('DB_PASSWORD', 'test_password')
    monkeypatch.setenv('DB_HOST', 'localhost')
    monkeypatch.setenv('DB_PORT', '5432')
    monkeypatch.setenv('DB_NAME', 'test_db')

# Fixture para configurar la aplicación y el cliente de prueba
@pytest.fixture
def client():
    app = create_app('src.config.TestConfig')  # Usa TestConfig para las pruebas
    app.config['TESTING'] = True

    # Forzar el contexto de la aplicación
    ctx = app.app_context()
    ctx.push()

    # Devolver el cliente de prueba
    yield app.test_client()

    # Al final, hacer pop del contexto
    ctx.pop()

# Mock del modelo Incidents
@patch('src.models.incidents.Incidents.query')
def test_get_incidents(mock_query, client):
    # Configurar el mock para que devuelva una lista vacía
    mock_query.all.return_value = []
    
    response = client.get('/incidents')
    assert response.status_code == 200
    assert response.data == b"[]\n"  # Verifica que la respuesta es una lista vacía


# Mock del modelo Incidents para emular una creación de incidente
@patch('src.models.incidents.Incidents')
@patch('src.extensions.db.session')
def test_post_incident(mock_db_session, mock_incident_model, client):
    # Configurar el mock para emular la creación de un incidente
    mock_incident = mock_incident_model.return_value
    mock_incident.id = 1
    
    response = client.post('/incidents', json={'subject': 'Server failing', 'description': 'Server down', 'originType': 'web', 'status': 'Open'})
    assert response.status_code == 200
    assert b"Incident created" in response.data

# Mock del modelo Incidents para un incidente no encontrado
from werkzeug.exceptions import NotFound
from unittest.mock import patch

@patch('src.models.incidents.Incidents.query')  # Asegúrate de que esté bien parcheado el atributo correcto
def test_get_incident_not_found(mock_query, client):
    # Configurar el mock para que lance una excepción NotFound cuando se busca un incidente inexistente
    mock_query.get_or_404.side_effect = NotFound(description="Incident not found")  # Simula la excepción 404 con el mensaje personalizado
    
    response = client.get('/incidents/9999')
    
    assert response.status_code == 404
    assert b"Incident not found" in response.data  # Ahora el mensaje debería estar en los datos de la respuesta
