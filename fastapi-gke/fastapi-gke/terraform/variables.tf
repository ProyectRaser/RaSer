variable "project_id" {
  description = "ID del proyecto de Google Cloud"
  type        = string
}

variable "region" {
  description = "Región donde se desplegará el clúster"
  type        = string
  default     = "us-west1"
}

variable "node_count" {
  description = "Cantidad de nodos del clúster"
  type        = number
  default     = 1
}

variable "slack_webhook_url" {
  description = "Webhook URL de Slack para Alertmanager"
  type        = string
}
