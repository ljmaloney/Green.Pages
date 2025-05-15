resource "aws_db_instance" "greenyp-psql-fa-db" {
  allocated_storage   = 20
  instance_class      = "db.t3.micro"
  engine              = "postgres"
  engine_version      = "13.4"
  db_name             = "fusionauth"
  username            = "fusionauth"
  password            = "e22eaecf-3b39-42a1-bd76-f0c2999c2338"
  identifier          = "green-yp-postgres-db"
  storage_type        = "gp2"
  publicly_accessible = false
  vpc_security_group_ids = [aws_security_group.greenyp-fusionauth-sg.id]
  db_subnet_group_name  = aws_db_subnet_group.green_yp_postgres-db-subnet-group.name
  multi_az            = true
  backup_retention_period = 7
  skip_final_snapshot = true
}
resource "aws_db_subnet_group" "green_yp_postgres-db-subnet-group" {
  name       = "green_yp_postgres-db-subnet-group"
  subnet_ids = [aws_subnet.greenyp-vpc-sn-a.id, aws_subnet.greenyp-vpc-sn-a.id]
  description = "FusionAuth RDS subnet group"
}
