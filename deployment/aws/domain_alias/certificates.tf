resource "aws_acm_certificate" "green_yp_cert" {
  provider          = aws.use1
  domain_name       = "green-yp.com"
  validation_method = "DNS"
}

resource "aws_acm_certificate" "greenyp_net_cert" {
  provider          = aws.use1
  domain_name       = "greenyp.net"
  validation_method = "DNS"
}

resource "aws_acm_certificate" "mygreenyp_cert" {
  provider          = aws.use1
  domain_name       = "mygreenyp.com"
  validation_method = "DNS"
}
