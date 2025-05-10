resource "aws_db_instance" "green-yp-psql-db1" {
  allocated_storage    = 20
  db_instance_class    = "db.t3.micro"
  engine               = "postgres"
  engine_version       = "13.4"
  db_name             = "fusionauth"
  username            = "fusionauth"
  password            = "e22eaecf-3b39-42a1-bd76-f0c2999c2338"
  instance_identifier = "green_yp_postgres_db"
  storage_type        = "gp2"
  publicly_accessible = false
  vpc_security_group_ids = [aws_security_group.greenyp_fusionauth_sg.id]
  db_subnet_group_name  = aws_db_subnet_group.green_yp_postgres_db_subnet_group.name
  multi_az            = true
  backup_retention_period = 7
  skip_final_snapshot = true
}
resource "aws_db_subnet_group" "green_yp_postgres_db_subnet_group" {
  name       = "green_yp_postgres_db_subnet_group"
  subnet_ids = [aws_subnet.greenyp_vpc_sn_a.id, aws_subnet.greenyp_vpc_sn_b.id]
  description = "FusionAuth RDS subnet group"
}
