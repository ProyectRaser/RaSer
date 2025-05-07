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
# Despliegue CI/CD con GitHub Actions

Este flujo de trabajo automatiza el proceso de integración continua (CI) y despliegue continuo (CD) para un proyecto Android utilizando **Gradle**, **SonarCloud**, **Snyk**, **Firebase**, y **Docker**.

## Objetivos

- **Pruebas unitarias**: Ejecuta pruebas automáticas y sube los resultados como artefactos.
- **Linting**: Analiza el código para detectar problemas de estilo y calidad.
- **Construcción**: Construye el APK y AAB, y los sube como artefactos.
- **Análisis de seguridad**: Ejecuta un análisis de vulnerabilidades con **Snyk**.
- **Docker**: Construye y sube una imagen Docker optimizada con el APK.
- **Firebase**: Subir el APK a Firebase para distribución.

## Jobs

### 1. **setup**
Configura el entorno de desarrollo, especificando la versión de **Java 17** y reutiliza un workflow común para el setup.

### 2. **test**
Ejecuta las pruebas unitarias con Gradle, mueve los resultados a un directorio específico y los sube como artefactos.

### 3. **lint**
Ejecuta el análisis de estilo de código con **Android Lint** y sube los informes de los problemas encontrados como artefactos.

### 4. **build**
Construye el proyecto, generando tanto el **APK** como el **AAB** y sube ambos artefactos. También ejecuta las pruebas unitarias.

### 5. **coverage**
Genera el reporte de cobertura de código usando **JaCoCo** y lo sube si el reporte se genera correctamente.

### 6. **sonarcloud-analysis**
Realiza un análisis de calidad de código con **SonarCloud**, utilizando las métricas de cobertura generadas por **JaCoCo**.

### 7. **security-analysis**
Ejecuta un análisis de vulnerabilidades con **Snyk**, escaneando todos los proyectos. También autentica y realiza el monitoreo de vulnerabilidades en el proyecto.

### 8. **publish-artifacts**
Sube el **APK** y el **AAB** generados a GitHub como artefactos para su distribución posterior.

### 9. **docker-release**
Crea una imagen **Docker** optimizada con el **APK** y la sube a **Docker Hub** para su distribución.

### 10. **firebase-upload**
Autentica con Firebase y sube el **APK** generado a **Firebase App Distribution**, permitiendo su distribución a testers.

### 11. **dependency-submission**
Envía el gráfico de dependencias a **SonarCloud** para obtener análisis de dependencias y problemas potenciales.

## Flujo de Trabajo

- **Inicio**: Cuando se hace un `push` o `pull_request` en la rama `master`, se inicia el flujo de trabajo.
- **Secuencia de Jobs**: Los jobs se ejecutan de manera secuencial y están condicionados por la finalización exitosa de los anteriores.
- **Artefactos**: Los resultados de pruebas, informes de lint, y artefactos de construcción se almacenan como artefactos, disponibles para su descarga o distribución.
- **Análisis de calidad y seguridad**: Se realiza análisis estático con **SonarCloud** y un análisis de vulnerabilidades con **Snyk** para garantizar la seguridad del código.
- **Distribución**: El APK generado se distribuye a Firebase y Docker Hub.




# Automatización del Despliegue del Backend en GKE con Terraform

## Descripción

En esta etapa del proyecto, se implementó un despliegue automatizado de la infraestructura esencial para la ejecución del backend, utilizando Terraform como herramienta de Infraestructura como Código (IaC). Se definió de manera declarativa un entorno completo en Google Cloud Platform (GCP), abarcando la creación de un clúster de Google Kubernetes Engine (GKE), la configuración de un Node Pool personalizado y la provisión de todos los recursos de Kubernetes necesarios, como secretos, volúmenes persistentes y manifiestos de despliegue de la aplicación.

Adicionalmente, se integró un sistema de observabilidad integral mediante la instalación automatizada del stack `kube-prometheus-stack` utilizando Helm. Esto permitió exponer métricas personalizadas del backend a Prometheus, visualizarlas en Grafana y configurar reglas de alerta personalizadas. Estas alertas son gestionadas por Alertmanager y se integran con Slack, facilitando el envío de notificaciones automáticas ante eventos críticos directamente al canal del equipo.

## Objetivos

* **Automatizar el despliegue de infraestructura** en Google Cloud Platform (GCP) mediante Terraform, siguiendo el enfoque de Infraestructura como Código (IaC).
* **Provisar un clúster de Kubernetes** en GKE con su correspondiente node pool configurado de forma declarativa.
* **Desplegar recursos Kubernetes** necesarios para ejecutar el backend (Deployment, Service, PVC, Secrets) sin intervención manual.
* **Integrar el stack de monitorización** utilizando Helm para instalar `kube-prometheus-stack` (Prometheus, Grafana, Alertmanager).
* **Exponer métricas personalizadas** del backend y capturarlas mediante `ServiceMonitor` para visualización en Grafana.
* **Definir reglas de alerta** con `PrometheusRule` y configurar Alertmanager para el envío de notificaciones automáticas a Slack.
* **Asegurar la trazabilidad y replicabilidad** del entorno completo mediante código versionado, reutilizable y fácilmente desplegable en distintos entornos.

## Arquitectura General

La arquitectura del proyecto se divide en tres capas principales:

* **Capa de Aplicación (FastAPI):**
    * API REST con endpoints para usuarios y tareas.
    * Persistencia con Firebase.
    * Exposición del endpoint `/metrics` para Prometheus.

* **Capa de Infraestructura (Terraform + Kubernetes):**
    * Clúster GKE con NodePools configurables.
    * Manifiestos Kubernetes para despliegue, servicios y volúmenes persistentes.
    * Gestión de secretos para Firebase y Alertmanager.

* **Capa de Observabilidad:**
    * Instalación del stack `kube-prometheus-stack` con Helm.
    * Definición de `ServiceMonitor` y `PrometheusRule` personalizados.
    * Dashboard en Grafana y alertas enviadas a Slack.

### Diagrama Lógico:



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



