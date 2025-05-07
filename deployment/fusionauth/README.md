# FusionAuth Deployment 

This file contains instructions for deployment of FusionAuth -  https://fusionauth.io/

The DockerCompose images can be found at https://fusionauth.io/docs/get-started/download-and-install/docker
Other instructions can found at https://fusionauth.io/docs/get-started/

The fusionauth-docker-compose.yaml will create an run and instance of fusion auth and related services in a local docker installation. 

## Amazon Web Services
All of the deployment files for AWS can be found under the aws directory in this folder. 

Install NGINX Ingress and cert manager
kubectl apply -f https://raw.githubusercontent.com/jetstack/cert-manager/master/deploy/charts/cert-manager/crds/clusterissuer.yaml
cd fusionauth-kustomize/overlays/prod; kubectl apply -k .

kubectl apply -f letsencrypt-prod-issuer.yaml

Install sealed secrets controller : kubectl apply -f https://github.com/bitnami-labs/sealed-secrets/releases/download/v0.24.4/controller.yaml

### Secrets
kubectl apply -f https://github.com/bitnami-labs/sealed-secrets/releases/download/v0.24.4/controller.yaml

kubectl create secret generic fusionauth-secret \
--from-literal=POSTGRES_USER=fusion \
--from-literal=POSTGRES_PASSWORD=fusionpass \
--dry-run=client -o yaml > secret.yaml

kubeseal --format yaml < secret.yaml > sealed-secret.yaml

kubectl apply -f sealed-secret.yaml

Update kustomize yaml for secrets
``env:
- name: DATABASE_ROOT_USERNAME
  valueFrom:
  secretKeyRef:
  name: fusionauth-secret
  key: POSTGRES_USER
- name: DATABASE_ROOT_PASSWORD
  valueFrom:
  secretKeyRef:
  name: fusionauth-secret
  key: POSTGRES_PASSWORD``

