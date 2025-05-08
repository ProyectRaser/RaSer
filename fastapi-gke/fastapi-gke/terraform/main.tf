# Obtener configuración del cliente GCP
data "google_client_config" "default" {}

# Crear clúster GKE
resource "google_container_cluster" "backend_cluster" {
  name                     = "backend-cluster"
  location                 = var.region
  remove_default_node_pool = true
  initial_node_count       = 1
  deletion_protection      = false
  node_locations           = ["us-west1-a"]
}

# Crear Node Pool
resource "google_container_node_pool" "primary_nodes" {
  name       = "backend-node-pool"
  cluster    = google_container_cluster.backend_cluster.name
  location   = var.region
  node_count = var.node_count

  node_config {
    machine_type = "e2-medium"
    disk_type    = "pd-standard"
    disk_size_gb = 20

    oauth_scopes = [
      "https://www.googleapis.com/auth/cloud-platform"
    ]
  }
}

# Renderizar alertmanager.yaml con Slack Webhook
resource "local_file" "alertmanager_config" {
  content  = templatefile("${path.module}/k8s/alertmanager.yaml.tpl", {
    SLACK_WEBHOOK_URL = var.slack_webhook_url
  })
  filename = "${path.module}/tmp/alertmanager.yaml"
}

# Crear Secret de Alertmanager
resource "null_resource" "create_alertmanager_secret" {
  provisioner "local-exec" {
    command = <<EOT
      gcloud container clusters get-credentials backend-cluster --region ${var.region} --project ${var.project_id}
      kubectl create namespace monitoring --dry-run=client -o yaml | kubectl apply -f -
      kubectl create secret generic alertmanager-kube-prometheus-alertmanager \
        --from-file=alertmanager.yaml=${path.module}/tmp/alertmanager.yaml \
        -n monitoring --dry-run=client -o yaml | kubectl apply -f -
    EOT
  }

  depends_on = [
    google_container_node_pool.primary_nodes,
    local_file.alertmanager_config
  ]
}

# Leer archivo local de firebase_key.json
locals {
  firebase_key_content = file("${path.module}/k8s/firebase_key.json")
}

# Crear Secret de Firebase
resource "kubernetes_secret" "firebase_key" {
  metadata {
    name      = "firebase-key-secret"
    namespace = "default"
  }

  data = {
    "firebase_key.json" = base64encode(local.firebase_key_content)
  }

  type = "Opaque"
}

# Aplicar manifiestos FastAPI
resource "null_resource" "apply_fastapi_resources" {
  provisioner "local-exec" {
    command = <<EOT
      gcloud container clusters get-credentials backend-cluster --region ${var.region} --project ${var.project_id}
      kubectl apply -f ${path.module}/k8s/pvc.yaml
      kubectl apply -f ${path.module}/k8s/deployment.yaml
      kubectl apply -f ${path.module}/k8s/service.yaml
    EOT
  }

  depends_on = [
    null_resource.create_alertmanager_secret,
    kubernetes_secret.firebase_key
  ]
}

# Instalar kube-prometheus-stack con Helm
resource "null_resource" "install_kube_prometheus_stack" {
  provisioner "local-exec" {
    command = <<EOT
      helm repo add prometheus-community https://prometheus-community.github.io/helm-charts || true
      helm repo update
      helm install kube-prometheus prometheus-community/kube-prometheus-stack \
        --namespace monitoring --create-namespace || true

      echo "Esperando CRDs..."
      until kubectl get crd servicemonitors.monitoring.coreos.com >/dev/null 2>&1; do sleep 5; done
      until kubectl get crd prometheusrules.monitoring.coreos.com >/dev/null 2>&1; do sleep 5; done
    EOT
  }

  depends_on = [null_resource.apply_fastapi_resources]
}

# Aplicar ServiceMonitor y regla de alerta
resource "null_resource" "apply_monitoring_resources" {
  provisioner "local-exec" {
    command = <<EOT
      kubectl apply -f ${path.module}/k8s/servicemonitor.yaml
      kubectl apply -f ${path.module}/k8s/prometheus-rule.yaml
    EOT
  }

  depends_on = [null_resource.install_kube_prometheus_stack]
}

# Reiniciar Alertmanager
resource "null_resource" "restart_alertmanager" {
  provisioner "local-exec" {
    command = <<EOT
      kubectl delete pod -l app.kubernetes.io/name=alertmanager -n monitoring
    EOT
  }

  depends_on = [null_resource.apply_monitoring_resources]
}

# Exponer Grafana, Prometheus y Alertmanager con LoadBalancer
resource "null_resource" "expose_monitoring_via_lb" {
  provisioner "local-exec" {
    command = <<EOT
      echo "Exponiendo Grafana como LoadBalancer..."
      kubectl expose deployment kube-prometheus-grafana \
        --type=LoadBalancer \
        --name=grafana-lb \
        --port=3000 \
        --target-port=3000 \
        -n monitoring --dry-run=client -o yaml | kubectl apply -f -

      echo "Patching Prometheus service..."
      kubectl patch svc kube-prometheus-kube-prome-prometheus \
        -n monitoring -p '{"spec": {"type": "LoadBalancer"}}'

      echo "Patching Alertmanager service..."
      kubectl patch svc kube-prometheus-kube-prome-alertmanager \
        -n monitoring -p '{"spec": {"type": "LoadBalancer"}}'
    EOT
  }

  depends_on = [null_resource.restart_alertmanager]
}
