import datetime
import os
import sys
import pytest
from unittest.mock import patch
from werkzeug.exceptions import NotFound

sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..', 'src')))

from src.app import create_app
from src.models.plan import Plan
from src.extensions import db

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

### Pruebas para PlanResource ###

# Mock del modelo Plan
@patch('src.models.plan.Plan.query')
def test_get_plan(mock_query, client):
    # Simular que ya existe un plan en la base de datos
    mock_query.first.return_value = Plan(id=1, plan="Test Plan", creationDate=datetime.datetime.now(), updateDate=datetime.datetime.now())
    
    response = client.get('/plan')
    assert response.status_code == 200
    data = response.get_json()
    assert data['plan'] == 'Test Plan'
    assert 'creationDate' in data
    assert 'updateDate' in data

@patch('src.models.plan.Plan.query')
def test_update_plan_not_found(mock_query, client):
    # Simular que no existe ningún plan
    mock_query.first.return_value = None
    
    response = client.put('/plan', json={'plan': 'Updated Plan'})
    assert response.status_code == 404
    assert b"Plan not found" in response.data

@patch('src.models.plan.Plan.query')
@patch('src.extensions.db.session')
def test_get_plan_update(mock_db_session, mock_query, client):
    # Simular que existe un plan en la base de datos
    mock_plan = Plan(id=1, plan="Test Plan", creationDate=datetime.datetime.now(), updateDate=datetime.datetime.now())
    mock_query.first.return_value = mock_plan
    
    # Actualizar el plan
    response = client.put('/plan', json={'plan': 'Updated Plan'})
    assert response.status_code == 200
    data = response.get_json()
    assert data['plan'] == 'Updated Plan'
    assert 'updateDate' in data
    mock_db_session.commit.assert_called_once()
