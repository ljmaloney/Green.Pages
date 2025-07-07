variable "source_domain" {}
variable "target_domain" {}
variable "hosted_zone_id" {
  description = "The Hosted Zone ID for the source domain in Route 53"
  type        = string
}
variable "acm_certificate_arn" {
  description = "ARN of the ACM certificate in us-east-1"
  type        = string
}

resource "aws_s3_bucket" "redirect_bucket" {
  bucket = var.source_domain
  force_destroy = true

  }
resource "aws_s3_bucket_website_configuration" "redirect_config" {
    bucket = aws_s3_bucket.redirect_bucket.id

    redirect_all_requests_to {
      host_name = var.target_domain
      protocol  = "https"
    }
  }

resource "aws_cloudfront_distribution" "redirect_dist" {
  enabled = true

  origin {
    domain_name = aws_s3_bucket.redirect_bucket.website_endpoint
    origin_id   = "s3-redirect-${var.source_domain}"

    custom_origin_config {
      http_port              = 80
      https_port             = 443
      origin_protocol_policy = "http-only"
      origin_ssl_protocols   = ["TLSv1.2"]
    }
  }

  default_cache_behavior {
    target_origin_id       = "s3-redirect-${var.source_domain}"
    viewer_protocol_policy = "redirect-to-https"
    allowed_methods        = ["GET", "HEAD"]
    cached_methods         = ["GET", "HEAD"]

    forwarded_values {
      query_string = false
      cookies {
        forward = "none"
      }
    }
  }

  viewer_certificate {
    acm_certificate_arn = var.acm_certificate_arn
    ssl_support_method  = "sni-only"
  }

  aliases = [var.source_domain]
}

resource "aws_route53_record" "alias_record" {
  zone_id = var.hosted_zone_id
  name    = var.source_domain
  type    = "A"

  alias {
    name                   = aws_cloudfront_distribution.redirect_dist.domain_name
    zone_id                = aws_cloudfront_distribution.redirect_dist.hosted_zone_id
    evaluate_target_health = false
  }
}
