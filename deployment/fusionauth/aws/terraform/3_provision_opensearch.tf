resource "aws_opensearch_domain" "green_yp_fusionauth_es" {
  domain_name = "green-yp-fusionauth-es"
  engine_version = "7.10"
  cluster_config {
    instance_type = "t3.small.search"
    instance_count = 2
    zone_awareness_enabled = true
    zone_awareness_config {
      availability_zone_count = 2
    }
  }
  ebs_options {
    ebs_enabled = true
    volume_size = 10
    volume_type = "gp2"
  }
  vpc_options {
    subnet_ids = [aws_subnet.greenyp-vpc-sn-a.id, aws_subnet.greenyp-vpc-sn-b.id]
    security_group_ids = [aws_security_group.greenyp-fusionauth-sg.id]
  }
}
