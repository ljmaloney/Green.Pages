resource "aws_route53_zone" "green_yp" {
  name = "green-yp.com"
}

resource "aws_route53_zone" "greenyp_net" {
  name = "greenyp.net"
}

resource "aws_route53_zone" "mygreenyp" {
  name = "mygreenyp.com"
}
