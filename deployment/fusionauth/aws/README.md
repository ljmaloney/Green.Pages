## Amazon Web Services
All of the deployment files for AWS can be found under the aws directory in this folder.
-- use setup_fusionauth_ec2.sh for installation using EC2 container (demo purposes only)

## Deployment using FARGATE and AWS services
1. Create AWS RDS for PostgreSQL
   1. Enable automatic backups and multi availablity zone deployments
   2. configure security groups so fusion auth can connect from fargate
   3. Fusion Auth needs to be in the same VPC or in a peer VPC
   4. Copy and save the hostname, port, and credentials
2. Set up AWS Open Search to supply Elastisearch functionality
   1. create a new OpenSearch domain
   2. configure instance types (t3.small.search) and enable auto-tune
   3. Enable vpc access
   4. Enable https endpoints
   5. Copy connection details (URL/Port) for use with fusionauth
3. Configure Amazon SES for sending email
   1. verify domain with SES console - add txt record to domain dns settings
   2. Setup SMTP configuration - AWS provides SMTP credentials
4. Deploy fusion auth to fargate
   1. Use docker-compose-fargate.yaml as a starting point
   2. Set the appropriate variables and credentials
   2. create task definition in fargate to use docker image
   3. fargate must be in same or peered VPC as PostgreSQL database, including a load balancer
   4. configure load balancer, and ensure security groups allow traffic to fargate cluster
