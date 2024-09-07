# MISW4501
# Componente Gestión Incidentes


## Instrucciones locales
====================================

Para ejecutar el proyecto localmente, se deben seguir los siguientes pasos:
> flask --app src.app run --host=0.0.0.0 --port=9876

Para ejecutar las pruebas unitarias, se deben seguir los siguientes pasos:
> pytest --cov-fail-under=70 --cov=src --cov-report=html


## Creación de imagen Docker
====================================

Docker build:
> docker build -t ms-incidents .

Docker run:
> docker run -p 9876:9876 ms-incidents


## Instrucciones con Google Cloud
====================================

Guía oficial de comandos de Google Cloud:
- https://cloud.google.com/sdk/gcloud/reference

## 1. Asegurar variables iniciales

```bash
gcloud config get-value project
```

Luego nos debemos autenticar en la consola de Google Cloud
```bash
gcloud auth configure-docker us-central1-docker.pkg.dev
```

```bash
gcloud auth login

gcloud config set project PROJECT_ID
```

## 2. Creación de imagen de contenedor

Debemos acceder a la carpeta del servicio que queremos desplegar y ejecutar el siguiente comando (incluya el punto del final):

```bash
cd <NOMBRE_SERVICIO>

docker build -t us-central1-docker.pkg.dev/<ID_PROYECTO>/<REPO_IMAGENES>/<NOMBRE_SERVICIO:VERSION> .

docker push us-central1-docker.pkg.dev/<ID_PROYECTO>/<REPO_IMAGENES>/<NOMBRE_SERVICIO:VERSION>
```

Recuerda que `build` es para construir la imagen y `push` para subirla al repositorio de imágenes (artifac registry) de Google Cloud.

Dependiendo del microservicio, los nombres de las imágenes pueden ser:
  > - us-central1-docker.pkg.dev/miso-dan-2024/uniandes-pf-2024/ms-incidents:1.0


Tenga en cuenta que el nombre del servicio debe ser el mismo que el nombre del directorio en el que se encuentra.

Adicional, en el ejemplo anterior se usan las versiones `1.0`, id de proyecto `miso-dan-2024` y el repositorio `uniandes-pf-2024` el cuál fue creado con anterioridad

Verificar que la imagen se haya subido correctamente en tu `<REPO_IMAGENES>` con el siguiente comando:
```bash
gcloud artifacts docker images list us-central1-docker.pkg.dev/miso-dan-2024/uniandes-pf-2024
```

Podemos probar la imagen (localmente) de la siguiente manera:
```bash
docker run -p 9876:9876 us-central1-docker.pkg.dev/miso-dan-2024/uniandes-pf-2024/ms-incidents:1.0
```

## 3. Creación de la red

Crea una red virtual para el despliegue de los servicios de Kubernetes. En este caso, he llamado la red `vpn-services-g18` y la he creado en la región `us-central1`:

```bash
gcloud compute networks create vpn-services-g18  --project=miso-dan-2024 --subnet-mode=custom --mtu=1460 --bgp-routing-mode=regional
```

Warning (it's not an error and it's not necessary to fix it yet):

Instances on this network will not be reachable until firewall rules are created. As an example, you can allow all internal traffic between instances as well as SSH, RDP, and ICMP by running:

```bash
gcloud compute firewall-rules create <FIREWALL_NAME> --network vpn-services-g18 --allow tcp,udp,icmp --source-ranges <IP_RANGE>

gcloud compute firewall-rules create <FIREWALL_NAME> --network vpn-services-g18 --allow tcp:22,tcp:3389,icmp
```

### Creación subred
-------------------

Una subred, o subred, es una pieza segmentada de una red más grande. Más específicamente, las subredes son una partición lógica de una red IP en varios segmentos de red más pequeños. Para crear la subred que se utilizará en el cluster de kubernetes, ejecute el comando:

```bash

gcloud compute networks subnets create vpn-users-service --range=192.168.32.0/19 --network=vpn-services-g18 --region=us-central1 --project=miso-dan-2024

```

Con esto, se crea una subred llamada `vpn-users-service` en la región `us-central1` con un rango de direcciones IP de `192.168.32.0/19` dentro de la red `vpn-services-g18`.

## 4. Creación de la base de datos

### Creación de la subred para la base de datos
-------------------

```bash
gcloud compute addresses create vpn-database-service --global --purpose=VPC_PEERING --addresses=192.168.0.0 --prefix-length=24 --network=vpn-services-g18 --project=miso-dan-2024
```

Por último, otorgue acceso a los servicios de administración de redes de GCP para que pueda realizar la gestión de la instancia a través de la red virtual creada:

```bash
gcloud services vpc-peerings connect --service=servicenetworking.googleapis.com --ranges=vpn-database-service --network=vpn-services-g18 --project=miso-dan-2024
```

Aplique la configuración del firewall para permitir el tráfico de la red virtual de Kubernetes a la red virtual de la base de datos:

```bash
gcloud compute firewall-rules create allow-db-g23-ingress --direction=INGRESS --priority=1000 --network=vpn-services-g18 --action=ALLOW --rules=tcp:5432 --source-ranges=192.168.1.0/24 --target-tags=databases --project=miso-dan-2024
```

### Despliegue de la base de datos

> Nombre: misw-g18-db

> Contraseña: misw-g18-db-pwd

> Versión: PostgreSQL 14

> Región: us-central1

>Disponibilidad zonal: Única

> Edición CLoud SQL:
>  - Enterprise
>  - Desarrollo


> Personalizar instancia:
> - Tipo de máquina: De núcleo compartido, 1 core y 1.7 GB de RAM
> - Almacenamiento 10 GB de HDD
> - No habilitar los aumentos automáticos de almacenamiento.


> Configure la configuración de red de la instancia según los siguientes datos:

> - Asignación de IP de la instancia: privada
> - Red: vpn-services-g18
> - Rango de IP asignado: vpn-database-service

Finalmente agregue la etiqueta de 'databases' a la instancia.

## 5. Despliegue en Cloud Run


Configuración de APIGateway
```bash
gcloud services enable apigateway.googleapis.com
```

URL: https://cloud.google.com/api-gateway/docs/get-started-cloud-run?hl=es-419

Aquí hay diferentes formas de realizar el despliegue, dependiendo de las necesidades del proyecto:

```bash
gcloud builds submit --tag gcr.io/PROJECT_ID/ms-incidents
```

```bash
> gcloud run deploy --image gcr.io/PROJECT_ID/ms-incidents --platform managed --region us-central1

> gcloud run deploy --image gcr.io/PROJECT_ID/ms-incidents --platform managed --region us-central1 --allow-unauthenticated

> gcloud run deploy --image gcr.io/PROJECT_ID/ms-incidents --platform managed --region us-central1 --allow-unauthenticated --memory=512Mi --cpu=1 --timeout=10m

> gcloud run deploy --image gcr.io/PROJECT_ID/ms-incidents --platform managed --region us-central1 --allow-unauthenticated --memory=512Mi --cpu=1 --timeout=10m --set-env-vars=ENVIRONMENT=production --set-env-vars=SECRET_KEY=secret --set-env-vars=DATABASE_URL=database --set-env-vars=LOG_LEVEL=INFO --set-env-vars=PORT=9876
```

1. Verificar el estado del despliegue.

```bash
gcloud run services list --platform managed
```

2. Verificar la URL del servicio desplegado.

```bash
gcloud run services describe ms-incidents --platform managed --region us-central1
```

3. Probar el servicio desplegado.

```bash
curl https://ms-incidents-<ID_PROYECTO>.a.run.app/incidents
```


## 6. API Gateway

1. Prepara tu Archivo OpenAPI

    Guarda tu archivo OpenAPI en un archivo local, por ejemplo, openapi2-run.yaml. Este archivo define las rutas y configuraciones de tu API.

2. Crea un Servicio en Google Cloud Run

    Si aún no has desplegado tu aplicación en Google Cloud Run, hazlo ahora. Asume que ya has construido y subido tu imagen de Docker a Google Container Registry.

    ```bash
    gcloud run deploy YOUR_CLOUD_RUN_SERVICE_NAME \
        --image gcr.io/YOUR_PROJECT_ID/YOUR_IMAGE \
        --platform managed \
        --region YOUR_REGION \
        --allow-unauthenticated
    ```

    Reemplaza:

        YOUR_CLOUD_RUN_SERVICE_NAME -> nombre de tu servicio en Cloud Run.
        
        YOUR_PROJECT_ID -> ID de tu proyecto de Google Cloud.
        
        YOUR_IMAGE -> imagen en Google Container Registry.
        
        YOUR_REGION -> región donde deseas desplegar tu servicio.


3. Crear una Configuración de API
Crea una configuración para tu API en Google Cloud API Gateway:

    ```bash
    gcloud api-gateway api-configs create apigateway-pf-config \
        --api=incidents-api \
        --openapi-spec=openapi2-run.yaml \
        --project=miso-dan-2024
    ```
    Reemplaza:

        YOUR_API_CONFIG_NAME -> nombre que desees para la configuración de la API.

        YOUR_API_NAME -> nombre de tu API.

        YOUR_BUCKET_NAME -> nombre del bucket donde subiste el archivo. Si usaste el SDK de Google Cloud, el nombre es el mismo que tienes en tu máquina si estás parado sobre ese directorio.

        YOUR_PROJECT_ID -> ID de tu proyecto de Google Cloud.


    Después de crear la configuración de la API, puedes ejecutar este comando para ver los detalles:

    ```
    gcloud api-gateway api-configs describe apigateway-pf-config \
    --api=incidents-api --project=miso-dan-2024
    ```

    Reemplaza:

        YOUR_API_CONFIG_NAME -> nombre de la configuración de tu API.

        YOUR_API_NAME -> nombre de tu API.

        YOUR_PROJECT_ID -> ID de tu proyecto de Google Cloud.
4. Crear un Gateway

    Crea un gateway que asocie tu API con la configuración:

    ```bash
    gcloud api-gateway gateways create misw-2024-api-gateway-pf \
        --api=incidents-api \
        --api-config=apigateway-pf-config \
        --location=us-central1 \
        --project=miso-dan-2024
    ```
    
    Reemplaza:

        YOUR_GATEWAY_NAME con el nombre que desees para el gateway.
        YOUR_API_NAME con el nombre de tu API.
        YOUR_API_CONFIG_NAME con el nombre de la configuración de tu API.
        YOUR_REGION con la región en la que deseas crear el gateway.
        YOUR_PROJECT_ID con el ID de tu proyecto de Google Cloud.

5. Verifica y Prueba tu Configuración

    Una vez que todo esté configurado, verifica la URL proporcionada por API Gateway y prueba las rutas definidas en tu archivo OpenAPI.

    Para obtener la URL del gateway, puedes usar el siguiente comando:

    ```bash
    gcloud api-gateway gateways describe misw-2024-api-gateway-pf --location=us-central1 --project=miso-dan-2024
    ```

    Reemplaza:

        YOUR_GATEWAY_NAME con el nombre de tu gateway.
        YOUR_REGION con la región en la que creaste tu gateway.
        YOUR_PROJECT_ID con el ID de tu proyecto de Google Cloud.

6. Cómo probar tu implementación de API

    Ahora puedes enviar solicitudes a tu API con la URL generada luego de la implementación de la puerta de enlace.

    Ingresa la siguiente URL en el navegador web, donde:

        DEFAULT_HOSTNAME -> host de la URL de la puerta de enlace implementada.
        incidents -> ruta especificada en la configuración del API.

    > https://DEFAULT_HOSTNAME/hello
    
    Por ejemplo:
    * https://my-gateway-a12bcd345e67f89g0h.uc.gateway.dev/incidents

## Endpoints

### Crear incidente
```
POST /incidents
```
#### Request
```json
{
    "title": "Incidente 1",
    "description": "Descripción del incidente 1",
    "priority": "Alta",
    "origin_type": "web",
    "status": "Abierto",
    "created_at": "2021-10-10T10:00:00",
    "updated_at": "2021-10-10T10:00:00"
}
```
#### Response
```json
{
    "id": 1,
    "title": "Incidente 1",
    "description": "Descripción del incidente 1",
    "priority": "Alta",
    "origin_type": "web",
    "status": "Abierto",
    "created_at": "2021-10-10T10:00:00",
    "updated_at": "2021-10-10T10:00:00"
}
```

### Listar incidentes
```
GET /incidents
```
#### Response
```json
[
    {
        "id": 1,
        "title": "Incidente 1",
        "description": "Descripción del incidente 1",
        "priority": "Alta",
        "status": "Abierto",
        "created_at": "2021-10-10T10:00:00",
        "updated_at": "2021-10-10T10:00:00"
    }
]
```
