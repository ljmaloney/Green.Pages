#!/bin/bash
set -e

DOMAIN="auth.mygreenyp.com"
EMAIL="luther.maloney@greenyp.com"  # For Let's Encrypt notifications

# Update and install dependencies
sudo apt update && sudo apt upgrade -y

# Add Docker's official GPG key:
sudo apt-get update
sudo apt-get install ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

# Add the repository to Apt sources:
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "${UBUNTU_CODENAME:-$VERSION_CODENAME}") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update

sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

sudo apt install -y nginx certbot python3-certbot-nginx ufw

# Enable and start Docker
sudo systemctl enable docker
sudo systemctl start docker

# Clone or set up your docker-compose.yml
sudo mkdir -p /opt/fusionauth
cd /opt/fusionauth

curl -o docker-compose.yml https://raw.githubusercontent.com/FusionAuth/fusionauth-containers/main/docker/fusionauth/docker-compose.yml
curl -o .env https://raw.githubusercontent.com/FusionAuth/fusionauth-containers/main/docker/fusionauth/.env

# Run the stack
sudo docker compose up -d

# NGINX Reverse Proxy for HTTPS
cat <<EOF | sudo tee /etc/nginx/sites-available/fusionauth
server {
    listen 80;
    server_name $DOMAIN;

    location / {
        proxy_pass http://localhost:9011;
        proxy_set_header Host \$host;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
}
EOF

sudo ln -s /etc/nginx/sites-available/fusionauth /etc/nginx/sites-enabled/
sudo nginx -t && sudo systemctl reload nginx

# Get SSL cert
sudo certbot --nginx -d $DOMAIN --non-interactive --agree-tos -m $EMAIL

# Enable UFW firewall
sudo ufw allow OpenSSH
sudo ufw allow 'Nginx Full'
sudo ufw --force enable
