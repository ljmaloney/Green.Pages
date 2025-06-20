# Steps for Deployment on AWS

This document contains the steps required to setup and configure an AWS Simple Storage Service (S3)
bucket for uploading/saving images uploaded via an a service running on an AWS Elastic
Container Services Fargate service/task. The bucket will be used to serve images to the public, and should
have read-only public permissions and read-write permissions for the Fargate service

### 1. Create an AWS - S3 Bucket

1. Login to AWS and navigate to S3, your user id should have permissions to create buckets
2. Click "Create Bucket"
3. Select "General Purpose"
4. Give the bucket a name

### 2. Setup Bucket Policy for Read Only access

In Bucket -> Permissions -> Bucket policy use something like the following to allow public read
access to the files in the bucket
<code>
{
"Version": "2012-10-17",
"Statement": [
{
"Sid": "AllowPublicRead",
"Effect": "Allow",
"Principal": "*",
"Action": "s3:GetObject",
"Resource": "arn:aws:s3:::greenyp-public-images/subscriber/*"
}
]
}
</code>

### 3. Setup IAM Permissions to Upload / Delete

In order to enable access for an AWS user and ECS, a policy like below is required to be configured for the IAM user and
the ECS Task Role
<code>
{
"Version": "2012-10-17",
"Statement": [
{
"Sid": "AppAccess",
"Effect": "Allow",
"Action": ["s3:PutObject", "s3:DeleteObject"],
"Resource": "arn:aws:s3:::greenyp-public-images/subscriber/*"
}
]
}
</code>

### 4. Disable Access Control Lists (Optional but Recommended)

This is one of the options available when creating a bucket, make sure to select "ACLs Disabled"