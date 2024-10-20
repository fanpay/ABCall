# DAN - G23


## Índice

1. [Estructura](#estructura)
2. [Ejecución](#ejecución)
3. [Uso](#uso)
4. [Pruebas](#pruebas)
5. [Autor](#autor)

## Estructura

Describe la estructura de archivos de la carpeta, puedes usar una estructura de arbol para ello:
```
├── .env # Archivo de variables de entorno
├── Dockerfile # Archivo de despliegue usando docker 
├── README.md # usted está aquí
├── requirements.txt # Archivo con las dependencias del proyecto
├── src # Carpeta con el código fuente del proyecto
│   ├── __init__.py
│   ├── blueprints # Carpeta con los blueprints de la aplicación
│   │   ├── offers.py
│   │   ├── ping.py
│   │   └──reset.py
│   ├── commands # Carpeta con los comandos de la aplicación
│   │   ├── __init__.py
│   │   ├── base_command.py # Archivo con la clase base de los comandos
│   │   ├── create.py 
│   │   ├── delete_offer.py 
│   │   ├── get_offer.py 
│   │   ├── list_filter_offers.py 
│   │   └── reset.py 
│   ├── errors # Carpeta con los errores de la aplicación
│   │   ├── __init__.py 
│   │   └── errors.py
│   ├── extensions.py # Archivo con las extensiones de la aplicación
│   ├── app.py # Archivo principal de la aplicación
│   └── models # Carpeta con los modelos de la aplicación
│       ├── __init__.py 
│       └── offer.py # Archivo con el modelo de usuario
└── tests # Carpeta con las pruebas de la aplicación
    ├── __init__.py 
    ├── commands # Carpeta con las pruebas de los comandos
    │   ├── test_bp_ping.py
    │   ├── test_bp_reset.py
    │   ├── test_cmd_create.py
    │   ├── test_cmd_delete.py
    │   ├── test_cmd_get_offer.py
    │   ├── test_cmd_list_filter.py
    │   ├── test_cmd_reset.py
    │   ├── test_errors.py
    │   └── test_model_offer.py
    └── conftest.py # Archivo de configuración de las pruebas
```
## Ejecución

### Ejecución local (desde archivos fuente):

Debe instalar las dependencias del proyecto antes de ejecutarlo. Se recomienda usar un entorno virtual.

> ```bash
> pip install --upgrade pip
> pip install -r requirements.txt
> ```

Ejecución de la aplicación de forma local. La aplicación se ejecutará en el puerto `5000`:

> ```bash
> FLASK_APP=./src/app.py flask run -h 0.0.0.0
> ```

### Ejecución de la aplicación de forma local como contenedor docker:

Debes crear primero la red para conectar la aplicación con la base de datos. Luego, ejecutar el contenedor de la base de datos y finalmente el de la aplicación.

> Creación de la red (`offer_net`): 

> ```bash
> docker network create offer_net
> docker network inspect offer_net
> ```

> Creación del contenedor de la base de datos: (Se crea el contenedor con la base de datos de postgres y se le asigna a la red `offer_net`)

> ```bash
> docker pull postgres
> docker run -d --name offers_management_db -e POSTGRES_USER=admin_db -e POSTGRES_PASSWORD=offer_db_pw -e POSTGRES_DB=offer_db -p 5434:5432 --network=offer_net postgres
> ```

> Creación del contenedor de la aplicación: (Se crea el contenedor con la aplicación y se le asigna a la red `offer_net`)

> ```bash 
> docker build -t offers_management_api .
> docker run -d -p 9876:9876 --name offers_management_api --network offer_net -e DB_USER=admin_db -e DB_PASSWORD=offer_db_pw -e DB_HOST=offers_management_db -e DB_PORT=5432 -e DB_NAME=offer_db offers_management_api
> ```

> Nota: Si la aplicación ya está ejecutándose en docker compose, sólo es apuntar al puerto `9876` del contenedor de la aplicación y la ruta `/offers` para hacer peticiones.

## Uso

El servicio de gestión de ofertas permite crear, eliminar, listar y filtrar ofertas.

Para usar el microservicio de usuarios, se deben hacer peticiones a la ruta `/offers` con los métodos `POST`, `GET` o `DELETE`.

### Endpoints
- > `POST /offers/ping`: Verifica que el microservicio esté en ejecución.
  - > ``` curl --location 'http://localhost:9876/offers/ping' ```
- Más información del contrato con información de cada servicio :arrow_right: [acá](https://github.com/MISW-4301-Desarrollo-Apps-en-la-Nube/proyecto-202411/wiki/Gesti%C3%B3n-de-Ofertas) :arrow_left:


## Pruebas

Para ejecutar las pruebas unitarias de los microservicios y establecer el porcentaje mínimo de cobertura del conjunto de pruebas en 70%, ejecuta el siguiente comando desde la carpeta `offers`:
> ```
> pytest --cov-fail-under=70 --cov=src
> pytest --cov-fail-under=70 --cov=src --cov-report=html
> ```

Este último comando crea una página HTML (`index.html`) con el reporte de cobertura de las pruebas en la carpeta `htmlcov` que se encuentra en la raíz de `offers`.

:warning: No olvides instalar las dependencias
> ``` bash
> pip install pytest pytest-cov
> ```

## Autor

Esneider Velandia Gonzalez - e.velandia2164@uniandes.edu.co
