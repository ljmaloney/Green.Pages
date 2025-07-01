variable "source_domain" {
  description = "Domain to redirect FROM"
  type        = string
}

variable "target_domain" {
  description = "Domain to redirect TO"
  type        = string
}

variable "hosted_zone_id" {
  description = "Hosted zone ID of the source domain"
  type        = string
}

variable "acm_certificate_arn" {
  description = "ACM cert for the source domain (must be in us-east-1)"
  type        = string
}
