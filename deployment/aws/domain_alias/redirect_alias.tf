module "redirect_green_yp" {
  source             = "./modules/domain_redirect"
  source_domain      = "green-yp.com"
  target_domain      = "greenyp.com"
  hosted_zone_id     = aws_route53_zone.green_yp.id
  acm_certificate_arn = aws_acm_certificate.green_yp_cert.arn
}

module "redirect_greenyp_net" {
  source             = "./modules/domain_redirect"
  source_domain      = "greenyp.net"
  target_domain      = "greenyp.com"
  hosted_zone_id     = aws_route53_zone.greenyp_net.id
  acm_certificate_arn = aws_acm_certificate.greenyp_net_cert.arn
}

module "redirect_mygreenyp" {
  source             = "./modules/domain_redirect"
  source_domain      = "mygreenyp.com"
  target_domain      = "greenyp.com"
  hosted_zone_id     = aws_route53_zone.mygreenyp.id
  acm_certificate_arn = aws_acm_certificate.mygreenyp_cert.arn
}
