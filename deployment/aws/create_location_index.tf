provider "aws" {
  region = "us-east-1"  # change to your region
}

# Create AWS Location Place Index
resource "aws_location_place_index" "greenyp" {
  index_name   = "GreenypPlaceIndex"
  data_source  = "Here"                      # or "Esri"
  pricing_plan = "RequestBasedUsage"
}

# IAM Policy allowing geo:SearchPlaceIndexForText on the Place Index
data "aws_caller_identity" "current" {}

resource "aws_iam_policy" "geo_search_policy" {
  name        = "GeoLocationSearchPolicy"
  description = "Allows search access to the AWS Location Place Index"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect   = "Allow"
        Action   = ["geo:SearchPlaceIndexForText"]
        Resource = aws_location_place_index.greenyp.arn
      }
    ]
  })
}

# Attach policy to an existing IAM role (e.g. ECS Task Role)
resource "aws_iam_role_policy_attachment" "attach_geo_policy" {
  role       = "your-ecs-task-role-name"  # replace with your ECS task role name
  policy_arn = aws_iam_policy.geo_search_policy.arn
}
