import os
from dotenv import load_dotenv

load_dotenv()

class Config:
    SQLALCHEMY_DATABASE_URI = f'postgresql://{os.environ.get("DB_USER", "default_user")}:{os.environ.get("DB_PASSWORD", "default_password")}@{os.environ.get("DB_HOST", "localhost")}:{os.environ.get("DB_PORT", "5432")}/{os.environ.get("DB_NAME", "test_db")}'
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    CACHE_TYPE = 'SimpleCache'  # Usar almacenamiento en memoria
    CACHE_DEFAULT_TIMEOUT = 300
    
class TestConfig(Config):
    TESTING = True
    SQLALCHEMY_DATABASE_URI = 'sqlite:///:memory:'  # Base de datos en memoria para pruebas
    SQLALCHEMY_TRACK_MODIFICATIONS = False