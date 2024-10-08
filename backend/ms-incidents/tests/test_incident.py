import datetime
import os
import sys
import pytest
from unittest.mock import patch

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..', 'src')))

from src.app import create_app
from src.models.incidents import Incidents

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
    mock_incident = mock_incident_model.return_value
    mock_incident.id = 1
    
    response = client.post('/incidents', json={'userId': '12345', 'subject': 'Server failing', 'description': 'Server down', 'originType': 'web', 'status': 'Open'})
    assert response.status_code == 201
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

@patch('src.models.incidents.Incidents.query')
def test_get_incidents_with_userid_no_results(mock_query, client):
    # Simula que no hay incidentes para un userId
    mock_query.filter_by.return_value.all.return_value = []
    
    response = client.get('/incidents?userId=12345')
    assert response.status_code == 200
    assert response.data == b"[]\n"  # Verifica que la respuesta es una lista vacía


@patch('src.models.incidents.Incidents.query')
def test_get_incidents_with_userid(mock_query, client):
    # Simula que hay incidentes para un userId
    mock_query.filter_by.return_value.all.return_value = [
        Incidents(id=1, userId='12345', subject='Test Incident', description='Test description', originType='web', status='OPEN', creationDate=datetime.datetime.now(), updateDate=datetime.datetime.now())
    ]
    
    response = client.get('/incidents?userId=12345')
    assert response.status_code == 200
    data = response.get_json()
    assert len(data) == 1
    assert data[0]['userId'] == '12345'


@patch('src.models.incidents.Incidents')
@patch('src.extensions.db.session')
@patch('src.extensions.cache.delete')
def test_post_incident_cache_deleted(mock_cache_delete, mock_db_session, mock_incident_model, client):
    mock_incident = mock_incident_model.return_value
    mock_incident.id = 1
    
    response = client.post('/incidents', json={'userId': '12345', 'subject': 'Server failing', 'description': 'Server down', 'originType': 'web', 'status': 'Open'})
    assert response.status_code == 201
    mock_cache_delete.assert_called_with('incidents_list')  # Verifica que la cache sea eliminada tras crear el incidente


@patch('src.models.incidents.Incidents.query')
def test_get_incidents_no_userid_with_results(mock_query, client):
    mock_query.all.return_value = [
        Incidents(id=1, userId='12345', subject='Test Incident', description='Test description', originType='web', status='OPEN', creationDate=datetime.datetime.now(), updateDate=datetime.datetime.now())
    ]
    
    response = client.get('/incidents')
    assert response.status_code == 200
    data = response.get_json()
    assert len(data) == 1
    assert data[0]['userId'] == '12345'

@patch('src.models.incidents.Incidents')
@patch('src.extensions.db.session')
def test_post_incident_missing_data(mock_db_session, mock_incident_model, client):
    # Intenta crear un incidente sin el campo obligatorio "description" ni "userId"
    response = client.post('/incidents', json={'subject': 'Server failing', 'originType': 'web'})
    assert response.status_code == 400  # Debería devolver un error si faltan datos obligatorios
    assert b"Missing fields: userId, description" in response.data


@patch('src.models.incidents.Incidents.query')
def test_get_incident_by_id(mock_query, client):
    # Simula que se encuentra un incidente
    mock_query.get_or_404.return_value = Incidents(
        id=1, userId='12345', subject='Test Incident', description='Test description',
        originType='web', status='OPEN', creationDate=datetime.datetime.now(), updateDate=datetime.datetime.now()
    )
    
    response = client.get('/incidents/1')
    assert response.status_code == 200
    data = response.get_json()
    assert data['id'] == 1
    assert data['userId'] == '12345'
    assert data['subject'] == 'Test Incident'


@patch('src.models.incidents.Incidents.query')
def test_get_incident_by_id_not_found(mock_query, client):
    # Simula que no se encuentra el incidente
    mock_query.get_or_404.side_effect = NotFound(description="Incident not found")
    
    response = client.get('/incidents/9999')
    assert response.status_code == 404
    assert b"Incident not found" in response.data


@patch('src.models.incidents.Incidents.query')
@patch('src.extensions.db.session')
def test_update_incident(mock_db_session, mock_query, client):
    # Simula que se encuentra un incidente
    mock_incident = mock_query.get_or_404.return_value
    mock_incident.id = 1
    mock_incident.userId = '12345'
    
    response = client.put('/incidents/1', json={'userId': '67890', 'subject': 'Updated Incident', 'description': 'Updated description', 'status': 'CLOSED'})
    
    assert response.status_code == 200
    assert b"Incident updated" in response.data
    mock_db_session.commit.assert_called_once()

@patch('src.models.incidents.Incidents.query')
@patch('src.extensions.db.session')
def test_delete_incident(mock_db_session, mock_query, client):
    # Simula que se encuentra un incidente
    mock_incident = mock_query.get_or_404.return_value
    mock_incident.id = 1
    
    response = client.delete('/incidents/1')
    
    assert response.status_code == 200
    assert b"Incident removed" in response.data
    mock_db_session.delete.assert_called_once_with(mock_incident)
    mock_db_session.commit.assert_called_once()
