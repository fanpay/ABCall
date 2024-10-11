# Despliegue usando Cloud Build

## Creación de la red

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

gcloud compute networks subnets create vpn-abcall-service --range=192.168.32.0/19 --network=vpn-services-g18 --region=us-central1 --project=miso-dan-2024

gcloud compute networks subnets create vpn-abcall-1-service --range=192.168.64.0/20 --network=vpn-services-g18 --region=us-central1 --project=miso-dan-2024

gcloud compute networks subnets create vpn-abcall-2-service --range=192.168.80.0/20 --network=vpn-services-g18 --region=us-central1 --project=miso-dan-2024

```

**OPCIONAL:**

Las subredes (vpn-abcall-1-service y vpn-abcall-2-service) son contiguas y se crean de la siguiente manera:

1. Subred en el rango 192.168.64.0/20
    - Rango de IPs: 192.168.64.0 a 192.168.79.255.
    - Esta subred está inmediatamente después del rango conflictivo (192.168.32.0/19).
2. Subred en el rango 192.168.80.0/20
    - Rango de IPs: 192.168.80.0 a 192.168.95.255.
    - Esta subred está inmediatamente después de la primera subred.

Con esto, se crea una subred llamada `vpn-users-service` en la región `us-central1` con un rango de direcciones IP de `192.168.32.0/19` dentro de la red `vpn-services-g18`.

> La creación de esta red es importante para comunicar internamente la aplicación Cloud Run con la base de datos Cloud SQL.



# Desplegar Cloud SQL

## Desplegar Cloud SQL con IP pública
```bash
gcloud deployment-manager deployments create misw-g18-db-deployment --config cloudsql-instance.yaml
```

## Ver operaciones
```
gcloud sql operations list --instance=misw-pf-db-instance
```

## Reintentar operación
```
gcloud deployment-manager deployments update misw-g18-db-deployment --config cloudsql-instance.yaml
```

## Deshabiitar IP publica (Opcional)

Luego de comprar que se puede acceder a la base de datos desde la máquina local, es buena práctica deshabilitar la IP pública.

```
gcloud sql instances patch misw-pf-db-instance --no-assign-ip
```

# Cloud Build
 
## Comando para desplegar desde la carpeta de infraestructura de manera manual

```
gcloud builds submit --config=cloudbuild.yaml ../
```

### Permisos

Agregue los siguientes permisos a la cuenta de servicio relacionada a Cloud Build:

```bash
gcloud config get-value project # YOUR_PROJECT_ID

gcloud projects add-iam-policy-binding miso-dan-2024 \
  --member=serviceAccount:719013485218@cloudbuild.gserviceaccount.com \
  --role=roles/run.admin

gcloud projects add-iam-policy-binding miso-dan-2024 \
  --member=serviceAccount:719013485218@cloudbuild.gserviceaccount.com \
  --role=roles/iam.serviceAccountUser
```

# Desplegar API Gateway

Configuración servicios de APIGateway

  URL de información: 
  * https://cloud.google.com/api-gateway/docs/get-started-cloud-run?hl=es-419

1. Prepara tu Archivo OpenAPI

    Guarda tu archivo OpenAPI en un archivo local, por ejemplo, openapi-run.yaml. Este archivo define las rutas y configuraciones de tu API.

2. Crea un Servicio en Google Cloud Run

    Si aún no has desplegado tu aplicación en Google Cloud Run, hazlo ahora. Asume que ya has construido y subido tu imagen de Docker a Google Container Registry.

3. Crear una Configuración de API
Crea una configuración para tu API en Google Cloud API Gateway:

    ```bash
    gcloud api-gateway api-configs create apigateway-pf-config \
        --api=abcall-api \
        --openapi-spec=openapi.yaml \
        --project=miso-dan-2024
    ```
    Reemplaza:

        YOUR_API_CONFIG_NAME -> nombre que desees para la configuración de la API.

        YOUR_API_NAME -> nombre de tu API.

        YOUR_OPENAPI_FILE -> nombre del archivo de OpenAPI con la configuración de la API.

        YOUR_PROJECT_ID -> ID de tu proyecto de Google Cloud.


    Después de crear la configuración de la API, puedes ejecutar este comando para ver los detalles:

    ```
    gcloud api-gateway api-configs describe apigateway-pf-config \
    --api=abcall-api --project=miso-dan-2024
    ```

    Reemplaza:

        YOUR_API_CONFIG_NAME -> nombre de la configuración de tu API.

        YOUR_API_NAME -> nombre de tu API.

        YOUR_PROJECT_ID -> ID de tu proyecto de Google Cloud.
4. Crear un Gateway

    Crea un gateway que asocie tu API con la configuración:

    ```bash
    gcloud api-gateway gateways create misw-2024-api-gateway-pf \
        --api=abcall-api \
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

5. Para actualizar el API Gateway (Opcional)

**Nota: Se debió crear una configuración diferente de la API Gateway**

  ```
  gcloud api-gateway gateways update misw-2024-api-gateway-pf \
    --api=abcall-api \
    --api-config=apigateway-pf-config-up1 \
    --location=us-central1 \
    --project=miso-dan-2024
  ```
6. Verifica y prueba tu configuración

    Una vez que todo esté configurado, verifica la URL proporcionada por API Gateway y prueba las rutas definidas en tu archivo OpenAPI.

    Para obtener la URL del gateway, puedes usar el siguiente comando:

    ```bash
    gcloud api-gateway gateways describe misw-2024-api-gateway-pf --location=us-central1 --project=miso-dan-2024
    ```

    Reemplaza:

        YOUR_GATEWAY_NAME con el nombre de tu gateway.
        YOUR_REGION con la región en la que creaste tu gateway.
        YOUR_PROJECT_ID con el ID de tu proyecto de Google Cloud.

7. Cómo probar tu implementación de API

    Ahora puedes enviar solicitudes a tu API con la URL generada luego de la implementación de la puerta de enlace.

    Ingresa la siguiente URL en el navegador web, donde:

        DEFAULT_HOSTNAME -> host de la URL de la puerta de enlace implementada.
        incidents -> ruta especificada en la configuración del API para incidentes.
        users -> ruta especificada en la configuración del API para usuarios.

    > https://DEFAULT_HOSTNAME/users/ping
    
    Por ejemplo:
    * https://my-gateway-a12bcd345e67f89g0h.uc.gateway.dev/users/ping