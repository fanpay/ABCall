from flask import Flask
from flask_restful import Api
from src.chatbot import ChatbotResource

app = Flask(__name__)
api = Api(app)

# Agregamos el recurso del chatbot a la API
api.add_resource(ChatbotResource, '/chat')

if __name__ == '__main__':
    app.run(debug=True, host='0.0.0.0', port=9878)
