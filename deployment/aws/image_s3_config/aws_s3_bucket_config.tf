resource "aws_s3_bucket" "greenyp" {
  bucket = "greenyp-public-images"
  force_destroy = true
}

resource "aws_s3_bucket_public_access_block" "greenyp" {
  bucket = aws_s3_bucket.greenyp.id

  block_public_acls       = false
  ignore_public_acls      = false
  block_public_policy     = false
  restrict_public_buckets = false
}

resource "aws_s3_bucket_versioning" "greenyp" {
  bucket = aws_s3_bucket.greenyp.id
  versioning_configuration {
    status = "Enabled"
  }
}

resource "aws_s3_bucket_policy" "greenyp" {
  bucket = aws_s3_bucket.greenyp.id

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Sid       = "AllowPublicRead",
        Effect    = "Allow",
        Principal = "*",
        Action    = "s3:GetObject",
        Resource  = "arn:aws:s3:::greenyp-public-images/subscriber/*"
      }
    ]
  })
}
