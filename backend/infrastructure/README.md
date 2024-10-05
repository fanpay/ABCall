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

OPCIONAL: 

Las subredes (vpn-abcall-1-service y vpn-abcall-2-service) son contiguas y se crean de la siguiente manera:

1. Subred en el rango 192.168.64.0/20
- Rango de IPs: 192.168.64.0 a 192.168.79.255.
- Esta subred está inmediatamente después del rango conflictivo (192.168.32.0/19).
2. Subred en el rango 192.168.80.0/20
- Rango de IPs: 192.168.80.0 a 192.168.95.255.
- Esta subred está inmediatamente después de la primera subred.

Con esto, se crea una subred llamada `vpn-users-service` en la región `us-central1` con un rango de direcciones IP de `192.168.32.0/19` dentro de la red `vpn-services-g18`.

> La creación de esta red es importante para comunicar internamente la aplicación Cloud Run con la base de datos Cloud SQL.



## Desplegar Cloud SQL

### Desplegar Cloud SQL con IP pública
> gcloud deployment-manager deployments create misw-g18-db-deployment --config cloudsql-instance.yaml

### Ver operaciones
> gcloud sql operations list --instance=misw-pf-db-instance

### Reintentar operación
> gcloud deployment-manager deployments update misw-g18-db-deployment --config cloudsql-instance.yaml

### Deshabiitar IP publica (Opcional)

Luego de comprar que se puede acceder a la base de datos desde la máquina local, es buena práctica deshabilitar la IP pública.

> gcloud sql instances patch misw-pf-db-instance --no-assign-ip


## Comando para desplegar desde la carpeta de infraestructura

> gcloud builds submit --config=cloudbuild.yaml ../

## Permissions

add the following permissions to the Cloud Build service account:

gcloud config get-value project # YOUR_PROJECT_ID

gcloud projects add-iam-policy-binding miso-dan-2024 \
  --member=serviceAccount:719013485218@cloudbuild.gserviceaccount.com \
  --role=roles/run.admin

gcloud projects add-iam-policy-binding miso-dan-2024 \
  --member=serviceAccount:719013485218@cloudbuild.gserviceaccount.com \
  --role=roles/iam.serviceAccountUser

