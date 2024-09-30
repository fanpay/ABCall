## Índice

1. [Ejecución](#ejecución)
2. [Uso](#uso)
3. [Pruebas](#pruebas)
4. [Autor](#autor)

## Ejecución

### Ejecución local (desde archivos fuente):

Debe instalar las dependencias del proyecto antes de ejecutarlo. Se recomienda usar un entorno virtual.

> ```bash
> pip install --upgrade pip
> pip install -r requirements.txt
> ```

Ejecución de la aplicación de forma local. La aplicación se ejecutará en el puerto `9878`:

> ```bash
> FLASK_APP=./src/main.py flask run -h 0.0.0.0 -p 9878
> ```

### Ejecución de la aplicación de forma local como contenedor docker:

Debes crear primero la red para conectar la aplicación con la base de datos. Luego, ejecutar el contenedor de la base de datos y finalmente el de la aplicación.

> Creación de la red (`user_net`): 

> ```bash
> docker network create user_net
> docker network inspect user_net
> ```

> Creación del contenedor de la base de datos: (Se crea el contenedor con la base de datos de postgres y se le asigna a la red `user_net`)

> ```bash
> docker pull postgres
> docker run -d --name users_management_db -e POSTGRES_USER=admin_db -e POSTGRES_PASSWORD=user_db_pw -e POSTGRES_DB=user_db -p 5434:5432 --network=user_net postgres
> ```

> Creación del contenedor de la aplicación: (Se crea el contenedor con la aplicación y se le asigna a la red `user_net`)

> ```bash 
> docker build -t users_management_api .
> docker run -d -p 9878:9876 --name users_management_api --network user_net -e DB_USER=admin_db -e DB_PASSWORD=user_db_pw -e DB_HOST=users_management_db -e DB_PORT=5432 -e DB_NAME=user_db users_management_api
> ```

> Nota: Si la aplicación ya está ejecutándose en docker compose, sólo es apuntar al puerto `9878` del contenedor de la aplicación y la ruta `/users` para hacer peticiones.

## Uso

El servicio de gestión de usuarios permite crear usuarios y validar la identidad de un usuario por medio de tokens.

Para usar el microservicio de usuarios, se deben hacer peticiones a la ruta `/users` con los métodos `POST`, `GET` o `PATCH`.

### Endpoints
- > `POST /users/ping`: Verifica que el microservicio esté en ejecución.
  - > ``` curl --location 'http://localhost:9878/users/ping' ```
- Más información del contrato con información de cada servicio :arrow_right: [acá](https://github.com/MISW-4301-Desarrollo-Apps-en-la-Nube/proyecto-202411/wiki/Gesti%C3%B3n-de-Usuarios) :arrow_left:


## Pruebas

Para ejecutar las pruebas unitarias de los microservicios y establecer el porcentaje mínimo de cobertura del conjunto de pruebas en 70%, ejecuta el siguiente comando desde la carpeta `users`:
> ```
> pytest --cov-fail-under=70 --cov=src
> pytest --cov-fail-under=70 --cov=src --cov-report=html
> ```

Este último comando crea una página HTML (`index.html`) con el reporte de cobertura de las pruebas en la carpeta `htmlcov` que se encuentra en la raíz de `users`.

:warning: No olvides instalar las dependencias
> ``` bash
> pip install pytest pytest-cov
> ```

## Autor

Esneider Velandia Gonzalez - e.velandia2164@uniandes.edu.co
