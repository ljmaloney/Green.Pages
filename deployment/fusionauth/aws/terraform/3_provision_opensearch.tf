resource "aws_opensearch_domain" "green_yp_fusionauth_es" {
  domain_name = "green_yp_fusionauth_es"
  opensearch_version = "2.19"
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
    subnet_ids = [aws_subnet.greenyp_vpc_sn_a.id, aws_subnet.greenyp_vpc_sn_b.id]
    security_group_ids = [aws_security_group.greenyp_fusionauth_iam_sg.id]
  }
}
