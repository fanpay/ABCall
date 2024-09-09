from flask import Flask
from flask_cors import CORS
from flask_restful import Api
from .config import Config
from .routes.incidents import IncidentsList, IncidentDetail
from .extensions import db, cache  


# Inicialización de la aplicación
app = Flask(__name__)
app.config.from_object(Config)

# Inicialización de extensiones
db.init_app(app)
cache.init_app(app)
CORS(app)
api = Api(app)

# Recursos Flask-RESTful
api.add_resource(IncidentsList, '/incidents')
api.add_resource(IncidentDetail, '/incidents/<int:id>')

with app.app_context():
    db.create_all()

app.app_context().push

if __name__ == '__main__':
    app.run(debug=True, port=9876)
