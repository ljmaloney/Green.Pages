resource "aws_lb" "gyp_fusionauth_lb" {
  name               = "fusionauth-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.greenyp-fusionauth-sg.id]
  subnets            = [aws_subnet.greenyp-vpc-sn-a.id, aws_subnet.greenyp-vpc-sn-b.id]
}

resource "aws_lb_target_group" "gyp_fusionauth_tgt_9011" {
  name     = "fusionauth-tg-9011"
  port     = 9011
  protocol = "HTTP"
  vpc_id   = aws_vpc.greenyp-main-vpc.id
}

resource "aws_lb_listener" "fusionauth_listener_9011" {
  load_balancer_arn = aws_lb.gyp_fusionauth_lb.arn
  port              = 9011
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.gyp_fusionauth_tgt_9011.arn
  }
}

resource "aws_lb_target_group_attachment" "fusionauth_attachment_9011" {
  target_group_arn = aws_lb_target_group.gyp_fusionauth_tgt_9011.arn
  target_id        = aws_ecs_service.fusionauth-service.id
  port             = 9011
}
