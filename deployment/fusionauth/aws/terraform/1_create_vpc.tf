# Create VPC
resource "aws_vpc" "greenyp-main-vpc" {
  cidr_block = "10.0.0.0/16"
  enable_dns_support = true
  enable_dns_hostnames = true
}
# Create Subnets
resource "aws_subnet" "greenyp-vpc-sn-a" {
  vpc_id                  = aws_vpc.greenyp-main-vpc.id
  cidr_block              = "10.0.1.0/24"
  availability_zone       = "us-east-1a"
  map_public_ip_on_launch = true
}
resource "aws_subnet" "greenyp-vpc-sn-b" {
  vpc_id                  = aws_vpc.greenyp-main-vpc.id
  cidr_block              = "10.0.2.0/24"
  availability_zone       = "us-east-1b"
  map_public_ip_on_launch = true
}
# Security Group for Fargate Tasks and RDS
resource "aws_security_group" "greenyp-fusionauth-sg" {
  name        = "greenyp-fusionauth-sg"
  vpc_id      = aws_vpc.greenyp-main-vpc.id
  description = "Allow traffic from Fargate to RDS & OpenSearch"
}
resource "aws_security_group_rule" "allow_gyp_fargate_to_rds" {
  type              = "ingress"
  from_port         = 5432
  to_port           = 5432
  protocol          = "tcp"
  security_group_id = aws_security_group.greenyp-fusionauth-sg.id
  source_security_group_id = aws_security_group.greenyp-fusionauth-sg.id
}
resource "aws_security_group_rule" "allow_fargate_to_es" {
  type              = "ingress"
  from_port         = 443
  to_port           = 443
  protocol          = "tcp"
  security_group_id = aws_security_group.greenyp-fusionauth-sg.id
  source_security_group_id = aws_security_group.greenyp-fusionauth-sg.id
}

resource "aws_security_group" "greenyp-inbound-sg" {
  name        = "greenyp-inbound-sg"
  vpc_id      = aws_vpc.greenyp-main-vpc.id
  description = "Allow traffic from Fargate to RDS & OpenSearch"
}
resource "aws_security_group_rule" "allow_http" {
  type              = "ingress"
  from_port         = 80
  to_port           = 80
  protocol          = "tcp"
  security_group_id = aws_security_group.greenyp-inbound-sg.id
  cidr_blocks       = ["0.0.0.0/0"]
}

resource "aws_security_group_rule" "allow_https" {
  type              = "ingress"
  from_port         = 443
  to_port           = 443
  protocol          = "tcp"
  security_group_id = aws_security_group.greenyp-inbound-sg.id
  cidr_blocks       = ["0.0.0.0/0"]
}

resource "aws_security_group_rule" "allow-custom-9011" {
  type              = "ingress"
  from_port         = 9011
  to_port           = 9011
  protocol          = "tcp"
  security_group_id = aws_security_group.greenyp-inbound-sg.id
  cidr_blocks       = ["0.0.0.0/0"]
}