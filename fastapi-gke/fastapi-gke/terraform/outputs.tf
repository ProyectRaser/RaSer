output "kubeconfig_command" {
  description = "Comando para conectar kubectl al clúster"
  value = "gcloud container clusters get-credentials backend-cluster --region ${var.region} --project ${var.project_id}"
}
