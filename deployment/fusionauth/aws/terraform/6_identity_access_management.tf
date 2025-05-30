resource "aws_iam_role" "gyp_fusionauth-task-role" {
  name = "gyp_fusionauth-task-role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action    = "sts:AssumeRole"
        Effect    = "Allow"
        Principal = {
          Service = "ecs-tasks.amazonaws.com"
        }
      }
    ]
  })
}
resource "aws_iam_role_policy_attachment" "gyp_fusionauth_task_policy_attachment" {
  role       = aws_iam_role.gyp_fusionauth-task-role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonRDSFullAccess"
}
