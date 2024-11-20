# ABCall Monorepo

Este repositorio contiene los diferentes componentes del proyecto ABCall, incluyendo el backend, frontend y la aplicación móvil.

## Índice

- [ABCall Monorepo](#abcall-monorepo)
  - [Índice](#índice)
  - [Estructura del Repositorio](#estructura-del-repositorio)
  - [Requisitos](#requisitos)
  - [Configuración del Entorno](#configuración-del-entorno)
    - [Backend](#backend)
    - [Frontend](#frontend)
    - [Mobile](#mobile)
  - [Ejecución](#ejecución)
    - [Backend](#backend-1)
    - [Frontend](#frontend-1)
    - [Mobile](#mobile-1)
  - [Pruebas](#pruebas)
    - [Backend](#backend-2)
    - [Frontend](#frontend-2)
    - [Mobile](#mobile-2)
  - [Despliegue](#despliegue)
    - [Backend](#backend-3)
    - [Frontend](#frontend-3)
    - [Mobile](#mobile-3)
    - [Extra :warning:](#extra-warning)
  - [Workflows Relacionados](#workflows-relacionados)
  - [Autores](#autores)

## Estructura del Repositorio

```plaintext
.
├── backend/
│   ├── collections/
│   ├── infrastructure/
│   ├── ms-chatbot/
│   ├── ms-dash-reports/
│   ├── ms-incidents/
│   └── ms-users/
├── frontend/
│   └── abcall-web/
├── mobile/
│   └── app/
└── README.md
```


## Requisitos

- Node.js
- Python 3.9
- Docker
- Android Studio (para la aplicación móvil)
- JDK 17
- Gradle

## Configuración del Entorno

### Backend

1. Clonar el repositorio:
    ```bash
    git clone https://github.com/fanpay/ABCall.git
    cd ABCall/backend
    ```

2. Crear un entorno virtual e instalar dependencias:
    ```bash
    python -m venv venv
    source venv/bin/activate
    pip install -r ms-chatbot/requirements.txt
    pip install -r ms-dash-reports/requirements.txt
    pip install -r ms-incidents/requirements.txt
    pip install -r ms-users/requirements.txt
    ```

### Frontend

1. Navegar al directorio del frontend:
    ```bash
    cd ABCall/frontend/abcall-web
    ```

2. Instalar dependencias:
    ```bash
    npm install
    ```

### Mobile

1. Abrir el proyecto en Android Studio:
    - Seleccionar "Open an existing Android Studio project".
    - Navegar a la carpeta `mobile` del repositorio clonado y abrirla.

2. Instalar dependencias:
    - Android Studio debería detectar automáticamente las dependencias y comenzar a descargarlas.

## Ejecución

### Backend

1. Ejecutar los microservicios localmente:
    ```bash
    flask --app ms-chatbot/src.app run --host=0.0.0.0 --port=9878
    flask --app ms-dash-reports/src.app run --host=0.0.0.0 --port=9879
    flask --app ms-incidents/src.app run --host=0.0.0.0 --port=9877
    flask --app ms-users/src.main run --host=0.0.0.0 --port=9876
    ```

### Frontend

1. Ejecutar la aplicación Angular:
    ```bash
    ng serve
    ```

2. Navegar a `http://localhost:4200/`.

### Mobile

1. Ejecutar la aplicación en un dispositivo o emulador desde Android Studio.

## Pruebas

### Backend

1. Ejecutar pruebas unitarias:
    ```bash
    pytest --cov=src --cov-fail-under=70 --cov-report=html
    ```

### Frontend

1. Ejecutar pruebas unitarias:
    ```bash
    npm run test -- --code-coverage --watch=false
    ```

### Mobile

1. Ejecutar pruebas unitarias:
    ```bash
    ./gradlew testDebugUnitTest
    ```

2. Generar reporte de cobertura con JaCoCo:
    ```bash
    ./gradlew jacocoTestReport
    ```

## Despliegue

### Backend

1. Desplegar usando Cloud Build:
    ```bash
    gcloud builds submit --config=cloudbuild.yaml ../../
    ```

### Frontend

1. Desplegar en Heroku:
    ```bash
    heroku container:push web --app abcall-uniandes
    heroku container:release web --app abcall-uniandes
    ```

### Mobile

1. Generar un APK de la aplicación:
    ```bash
    ./gradlew assembleDebug
    ```


### Extra :warning:
**Para más información y mucha más precisión sobre el despliegue de los microservicios, revisar el archivo [README.md](https://github.com/fanpay/ABCall/blob/main/backend/infrastructure/README.md)**

## Workflows Relacionados

- [Manual - BE - Ejecución de pruebas unitarias en microservicios](https://github.com/fanpay/ABCall/actions/workflows/be_manual_unit_testing.yml)
- [Pruebas e integración de ramas de funcionalidad - BackEnd](https://github.com/fanpay/ABCall/actions/workflows/be_integration.yml)
- [Manual - FE - Ejecución de pruebas unitarias en frontend Angular](https://github.com/fanpay/ABCall/actions/workflows/fe_manual_unit_testing.yml)
- [Pruebas e integración de ramas de funcionalidad - FrontEnd](https://github.com/fanpay/ABCall/actions/workflows/fe_integration.yml)
- [Manual - MB - Ejecución de pruebas unitarias](https://github.com/fanpay/ABCall/actions/workflows/mb_manual_unit_testing.yml)
- [Pruebas e integración de ramas de funcionalidad - Mobile](https://github.com/fanpay/ABCall/actions/workflows/mb_integration.yml)

## Autores

- Fabián Andrés Payan Meneses - f.payan@uniandes.edu.co
- Esneider Velandia Gonzalez - e.velandia2164@uniandes.edu.co