resource "aws_ecs_cluster" "fusionauth_cluster" {
  name = "fusionauth-cluster"
}
resource "aws_ecs_task_definition" "fusionauth_task" {
  family                   = "fusionauth-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"

  container_definitions = jsonencode([
    {
      name      = "fusionauth"
      image     = "fusionauth/fusionauth-app:latest"
      essential = true
      environment = [
        {
          name  = "DATABASE_URL"
          value = "jdbc:postgresql://${aws_db_instance.green_yp_postgres_db.address}:5432/fusionauth"
        },
        {
          name  = "DATABASE_USERNAME"
          value = "fusionauth"
        },
        {
          name  = "DATABASE_PASSWORD"
          value = "your_password_here"
        },
        {
          name  = "SEARCH_URL"
          value = "${aws_opensearch_domain.green_yp_fusionauth_es.endpoint}"
        },
        {
          name  = "FUSIONAUTH_EMAIL_HOST"
          value = "email-smtp.us-east-1.amazonaws.com"
        },
        {
          name  = "FUSIONAUTH_EMAIL_PORT"
          value = "587"
        },
        {
          name  = "FUSIONAUTH_EMAIL_USERNAME"
          value = "SES_SMTP_USERNAME"
        },
        {
          name  = "FUSIONAUTH_EMAIL_PASSWORD"
          value = "SES_SMTP_PASSWORD"
        },
        {
          name  = "FUSIONAUTH_EMAIL_FROM_ADDRESS"
          value = "no-reply@example.com"
        }
      ]
    }
  ])
}
resource "aws_ecs_service" "fusionauth_service" {
  name            = "fusionauth-service"
  cluster         = aws_ecs_cluster.fusionauth_cluster.id
  task_definition = aws_ecs_task_definition.fusionauth_task.arn
  desired_count   = 1
  launch_type     = "FARGATE"
  network_configuration {
    subnets          = [aws_subnet.greenyp_vpc_sn_a.id, aws_subnet.greenyp_vpc_sn_b.id]
    security_groups = [aws_security_group.greenyp_fusionauth_iam_sg.id]
    assign_public_ip = true
  }
}
