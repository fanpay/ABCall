## Índice

1. [Ejecución](#ejecución)
2. [Uso](#uso)
3. [Pruebas](#pruebas)
4. [Autor](#autor)

## Ejecución

### Ejecución local (desde archivos fuente)

Debe instalar las dependencias del proyecto antes de ejecutarlo. Se recomienda usar un entorno virtual.

> ```bash
> pip install --upgrade pip
> pip install -r requirements.txt
> ```

Ejecución de la aplicación de forma local. La aplicación se ejecutará en el puerto `9878`:

> ```bash
> flask --app src.app run --host=0.0.0.0 --port=9878
> ```

### Ejecución de la aplicación de forma local como contenedor docker


> Creación del contenedor de la aplicación:

> ```bash 
> docker build -t chatbot_api .
> docker run -d -p 9878:9878 --name chatbot_api -e INCIDENT_SERVICE_URL=http://<localhost>:9877/incidents -e USERS_SERVICE_URL=http://<localhost>:9876/users -e CHATBOT_RULES_FILE=src/rules.txt
> ```

> Nota: Si la aplicación ya está ejecutándose en docker compose, sólo es apuntar al puerto `9878` del contenedor de la aplicación y la ruta `/chat` para hacer peticiones.

## Uso

El servicio de chatbot permite enviar mensajes a un chatbot y recibir respuestas.

Para usar el microservicio de usuarios, se deben hacer peticiones a la ruta `/chat`.

Revisa la [colección de POSTMAN](https://github.com/fanpay/ABCall/blob/main/backend/collections/ABCall.postman_collection.json) para conocer los detalles de los endpoints.



## Pruebas

Para ejecutar las pruebas unitarias de los microservicios y establecer el porcentaje mínimo de cobertura del conjunto de pruebas en 70%, ejecuta el siguiente comando desde la carpeta `users`:
> ```
> pytest --cov-fail-under=70 --cov=src
> pytest --cov-fail-under=70 --cov=src --cov-report=html
> ```

Este último comando crea una página HTML (`index.html`) con el reporte de cobertura de las pruebas en la carpeta `htmlcov` que se encuentra en la raíz de `ms-chatbot`.

:warning: No olvides instalar las dependencias
> ``` bash
> pip install pytest pytest-cov
> ```


## Workflows relacionados
* [Manual - BE - Ejecución de pruebas unitarias en microservicios](https://github.com/fanpay/ABCall/actions/workflows/be_manual_unit_testing.yml)
* [Pruebas e integración de ramas de funcionalidad - BackEnd](https://github.com/fanpay/ABCall/actions/workflows/be_integration.yml)


## Autores

Fabián Andrés Payan Meneses - f.payan@uniandes.edu.co <br/>
Esneider Velandia Gonzalez - e.velandia2164@uniandes.edu.co
