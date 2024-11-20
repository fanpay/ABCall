# MISW4501
# Componente Gestión Incidentes


## Instrucciones locales

Para ejecutar el proyecto localmente, se deben seguir los siguientes pasos:
> flask --app src.app run --host=0.0.0.0 --port=9877

Para ejecutar las pruebas unitarias, se deben seguir los siguientes pasos:
> pytest --cov-fail-under=70 --cov=src --cov-report=html

No olvides crear un archivo `.env` con las siguientes variables de entorno:
```
VERSION=1.0.prod
DB_USER=postgres
DB_PASSWORD=postgres
DB_HOST=localhost
DB_PORT=5433
DB_NAME=ms_incidents_db
````

:warning: Recuerda modificar las variables de entorno según tu configuración local.

Para construir la imagen de Docker, se deben seguir los siguientes pasos:

```
docker build -t ms_incidents .

docker run -d -p 9877:9877 --name ms_incidents -e DB_USER=postgres -e DB_PASSWORD=postgres -e DB_HOST=localhost -e DB_PORT=5433 -e DB_NAME=ms_incidents_db ms_incidents
```

## Uso

El servicio de gestión de incidentes permite crear, listar y obtener incidentes.

Para usar el microservicio de usuarios, se deben hacer peticiones a la ruta `/incidents`.

Revisa la [colección de POSTMAN](https://github.com/fanpay/ABCall/blob/main/backend/collections/ABCall.postman_collection.json) para conocer los detalles de los endpoints.

## Workflows relacionados
* [Manual - BE - Ejecución de pruebas unitarias en microservicios](https://github.com/fanpay/ABCall/actions/workflows/be_manual_unit_testing.yml)
* [Pruebas e integración de ramas de funcionalidad - BackEnd](https://github.com/fanpay/ABCall/actions/workflows/be_integration.yml)


## Autores

Fabián Andrés Payan Meneses - f.payan@uniandes.edu.co <br/>
Esneider Velandia Gonzalez - e.velandia2164@uniandes.edu.co
