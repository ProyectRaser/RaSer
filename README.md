## RaSer

**RaSer** es una aplicación móvil enfocada en la **gestión de tareas para profesionales DevOps**, diseñada para facilitar el seguimiento, organización y filtrado de tareas técnicas de forma rápida e intuitiva.

### 🧩 Funcionamiento

- **Registro sencillo**: Solo necesitas ingresar un correo electrónico y una contraseña para crear una cuenta.
- **Completar perfil**: Al registrarte, serás redirigido a una pantalla para completar tu información personal.
- **Gestión de tareas**: La interfaz principal permite añadir, editar y eliminar tareas fácilmente. Al crear una tarea, se despliega un formulario con campos personalizables.

### 📋 Campos de la tarea

Cada tarea cuenta con los siguientes campos:

- **Estado**: Por hacer, En proceso, Completada.
- **Prioridad**: Baja (🟢), Media (🟠), Alta (🔴).
- **Lenguaje**: Lenguaje principal relacionado con la tarea (por ejemplo, Python, Java...).
- **Tipo**: Tipo de tarea (bug, mejora, nueva funcionalidad...).
- **Entorno**: Entorno donde se desarrolla (producción, staging, local...).

---

### 📌 Detalle de los campos de la tarea

- **Estado**  
  Indica el progreso actual de la tarea:
  - **Por hacer**: La tarea está registrada pero aún no se ha comenzado.
  - **En proceso**: La tarea se encuentra actualmente en desarrollo o ejecución.
  - **Completada**: La tarea ya fue finalizada con éxito.

- **Prioridad**  
  Permite visualizar rápidamente la urgencia o importancia de la tarea. Se representa con colores:
  - **Baja (🟢)**: No es urgente; puede resolverse en cualquier momento.
  - **Media (🟠)**: Tiene cierta prioridad, conviene atenderla pronto.
  - **Alta (🔴)**: Requiere atención inmediata.

- **Lenguaje**  
  Lenguaje de programación principal asociado a la tarea. Ejemplos comunes incluyen:
  - Python, Java, JavaScript, Kotlin, etc.

- **Tipo**  
  Clasifica la naturaleza de la tarea:
  - **Bug**: Corrección de errores o fallos en el sistema.
  - **Mejora**: Optimización o refactorización de funcionalidad existente.
  - **Nueva funcionalidad**: Desarrollo de características nuevas.

- **Entorno**  
  Define el contexto en el que se ejecuta o se prueba la tarea:
  - **Producción**: Sistema en funcionamiento real, usado por los usuarios finales.
  - **Staging**: Entorno previo a producción, para pruebas finales.
  - **Local**: Entorno de desarrollo personal del programador.

---

### 🔎 Filtros inteligentes

Puedes **filtrar tareas por cualquiera de los campos anteriores**, lo que permite un control más eficiente según el estado, la prioridad, el entorno de ejecución, etc.

### 🎯 Características clave

- Interfaz intuitiva y optimizada para móviles.
- Autenticación con correo electrónico y Google.
- Posibilidad de editar y eliminar tareas.
- Filtros combinables para encontrar tareas fácilmente.
- Almacenamiento seguro con Firebase Firestore.
- Subida de imagen de perfil y gestión de cuenta.

---

### Automatización del despliegue del backend en GKE con terraform.

**Descripción**

En esta etapa del proyecto se implementó un proceso de despliegue y monitorización completamente automatizado para una aplicación backend en producción. Utilizando Terraform como herramienta de Infraestructura como Código (IaC), se aprovisionó de forma declarativa un entorno completo en Google Cloud Platform (GCP), incluyendo la creación de un clúster de Google Kubernetes Engine (GKE), su configuración con un Node Pool personalizado, y la gestión de secretos y recursos Kubernetes necesarios para ejecutar la aplicación.

Como parte fundamental del despliegue, se integró un sistema de observabilidad completo, instalando automáticamente el stack de monitorización kube-prometheus-stack mediante Helm. Esto permitió la exposición de métricas a través de Prometheus, la visualización en tiempo real mediante Grafana, y la configuración de alertas automáticas con Alertmanager, integradas con Slack para la notificación de eventos críticos.

Este enfoque garantiza que todo el ciclo de vida de la infraestructura y la monitorización del backend pueda ser reproducido, versionado y desplegado de forma consistente en diferentes entornos, minimizando errores manuales y asegurando una operación confiable.

**Objetivos**

- Automatizar el despliegue de infraestructura en Google Cloud Platform (GCP) mediante Terraform, siguiendo el enfoque de Infraestructura como Código (IaC).

- Provisar un clúster de Kubernetes en GKE con su correspondiente node pool configurado de forma declarativa.

- Desplegar recursos Kubernetes necesarios para ejecutar el backend (Deployment, Service, PVC, Secrets) sin intervención manual.

- Integrar el stack de monitorización utilizando Helm para instalar kube-prometheus-stack (Prometheus, Grafana, Alertmanager).

- Exponer métricas personalizadas del backend y capturarlas mediante ServiceMonitor para visualización en Grafana.

- Definir reglas de alerta con PrometheusRule y configurar Alertmanager para el envío de notificaciones automáticas a Slack.

- Asegurar la trazabilidad y replicabilidad del entorno completo mediante código versionado, reutilizable y fácilmente desplegable en distintos entornos.


**Arquitectura general**

La arquitectura del proyecto se divide en tres capas principales:

1.	Capa de Aplicación (FastAPI)
o	API REST con endpoints para usuarios y tareas.
o	Persistencia con Firebase.
o	Exposición del endpoint /metrics para Prometheus.
2.	Capa de Infraestructura (Terraform + Kubernetes)
o	Clúster GKE con NodePools configurables.
o	Manifiestos Kubernetes para despliegue, servicios y volúmenes persistentes.
o	Gestión de secretos para Firebase y Alertmanager.
3.	Capa de Observabilidad
o	Instalación del stack kube-prometheus-stack con Helm.
o	Definición de ServiceMonitor y PrometheusRule personalizados.
o	Dashboard en Grafana y alertas enviadas a Slack.

**Diagrama lógico:**
 
```bash

Usuario ─▶ [ FastAPI App ] ─▶ [ Firestore (Firebase) ]
            │        │
            │        └▶ [ /metrics → Prometheus ]
            ▼
        Kubernetes (GKE) ─▶ [ Prometheus / Grafana / Alertmanager ]

```

Este diseño modular permite escalar la aplicación, mantener la observabilidad y responder rápidamente ante fallos mediante alertas automáticas.



## Tecnologías utilizadas.


•	FastAPI

•	Firebase Firestore

•	Prometheus

•	Grafana

•	Alertmanager

•	Terraform

•	Helm

•	Google Kubernetes Engine (GKE)

•	Kubernetes



---

## Estructura del despliegue backend FastAPI

```sh
 
backend/
│
├── src/
│   ├── main.py              Código principal de FastAPI + métricas
│   ├── firebase.py          Conexión a Firestore
│
├── k8s/
│   ├── firebase_key.json
│   ├── deployment.yaml
│   ├── service.yaml
│   ├── pvc.yaml
│   ├── servicemonitor.yaml
│   ├── prometheus-rule.yaml
│   └── alertmanager.yaml.tpl
│
├── fastapi-gke/terraform/
│   ├── main.tf
│   ├── variables.tf
│   ├── outputs.tf
│   └── terraform.tfvars

```
---

## Desarrollo del backend.

El backend se desarrolló con FastAPI y utiliza Firestore para almacenar datos de usuarios y tareas. Además, expone un endpoint /metrics con métricas personalizadas, lo que permite su monitorización con Prometheus.

**Características principales** 

- Endpoints REST para gestión de usuarios y tareas.

- Exposición de métricas en /metrics, incluyendo:

 - Contador de peticiones por endpoint (Counter).

 - Uso de CPU del proceso (Gauge).

 - Total de usuarios y tareas en Firestore.

**Librerías utilizadas** 

- fastapi y uvicorn: servidor web y API REST

- firebase_admin: conexión con Firebase Firestore

- prometheus_client: exposición de métricas

- psutil: monitoreo de recursos del sistema

---

## Despliegue en kubernetes con terraform.

La infraestructura se automatiza completamente usando Terraform.
El clúster de Kubernetes se crea en GKE, y los recursos adicionales se aplican con manifiestos YAML.

**Clúster GKE**

   
 ```sh 
resource "google_container_cluster" "backend_cluster" {
  name     = "backend-cluster"
  location = "us-west1-a"
}

```

**NodePool**

 ```sh

   
resource "google_container_node_pool" "primary_nodes" {
  machine_type = "e2-medium"
  node_count   = var.node_count
}

```

**Secrets**

•	Se crea un secreto con la clave de Firebase (firebase_key.json)
•	Se utiliza una plantilla para generar el secreto de configuración de Alertmanager con el webhook de Slack (alertmanager.yaml.tpl)

**Manifiestos aplicados**

```sh
kubectl apply -f pvc.yaml
kubectl apply -f deployment.yaml
kubectl apply -f service.yaml

```

---

## Configuración de monitorización

**Instalación del Stack**

helm install kube-prometheus prometheus-community/kube-prometheus-stack

**Componentes incluidos:**
•	Prometheus: recolector de métricas.
•	Grafana: visualización de métricas.
•	Alertmanager: gestión de alertas.

**Recursos personalizados**
•	servicemonitor.yaml para detectar métricas de la app.
•	prometheus-rule.yaml con reglas como uso elevado de CPU.
•	alertmanager.yaml.tpl con integración Slack.

**Exposición de servicios**
•	Grafana en puerto 3000.
•	Prometheus y Alertmanager en puertos (9090, 9093).

---


##Pasos de Ejecución.

**Inicializar Terraform**
 
```sh

terraform init

```

Inicializa el proyecto y descarga los proveedores necesarios.

**Revisar el plan de despliegue**

 ```sh
    
terraform plan

```
Permite verificar los recursos que se crearán/modificarán.

**Aplicar el despliegue**

```sh
    
terraform apply
```

**Ejecuta las acciones definidas:**

1.	Creación del clúster y nodos
2.	Aplicación de secretos (Firebase, Alertmanager)
3.	Despliegue de FastAPI y recursos Kubernetes
4.	Instalación del stack de monitorización
5.	Configuración de reglas de Prometheus y reinicio de Alertmanager
6.	Exposición de los servicios

---

## Verificaciones y Pruebas

**Infraestructura**

 ```sh

gcloud container clusters list
gcloud container clusters get-credentials backend-cluster --region us-west1

```

**Kubernetes**

```sh
 
kubectl get pods
kubectl get svc
kubectl get pvc
kubectl get secret
kubectl get deployment

```

**Verificar que:**
•	El pod de FastAPI esté en estado Running
•	El PVC esté correctamente enlazado
•	El servicio esté expuesto correctamente
•	El secreto de Firebase esté presente

**Monitorización**

```sh
   
kubectl get pods -n monitoring
kubectl get svc -n monitoring

```

**Verificar que:**
•	Grafana, Prometheus y Alertmanager estén desplegados
•	Los servicios tengan una IP externa (LoadBalancer)ç

**Acceso a interfaces**
Obtener las IPs de los servicios:
 
```sh
  
kubectl get svc -n monitoring

```

**Acceder desde el navegador:**
•	Grafana: http://<EXTERNAL-IP>:3000
•	Prometheus: http://<EXTERNAL-IP>:9090
•	Alertmanager: http://<EXTERNAL-IP>:9093

**Verificación final**
•	Confirmar que se reciben métricas en Grafana
•	Simular errores (alto uso de CPU, caída del pod)
•	Verificar llegada de alertas en Slack



