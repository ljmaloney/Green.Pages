CLUSTER_NAME := fusionauth-demo
NAMESPACE := fusionauth
EMAIL := your@email.com

.PHONY: all bootstrap deploy ingress cert secrets clean

all: bootstrap ingress cert secrets deploy

bootstrap:
	@echo "🔧 Creating namespace..."
	kubectl create namespace $(NAMESPACE) --dry-run=client -o yaml | kubectl apply -f -
	@echo "✅ Namespace created."

ingress:
	@echo "🚀 Installing NGINX Ingress Controller..."
	kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.10.0/deploy/static/provider/cloud/deploy.yaml
	@echo "⏳ Waiting for ingress controller to be ready..."
	kubectl wait --namespace ingress-nginx \
	  --for=condition=ready pod \
	  --selector=app.kubernetes.io/component=controller \
	  --timeout=120s
	@echo "✅ Ingress controller installed."

cert:
	@echo "🔐 Installing cert-manager..."
	kubectl apply -f https://github.com/jetstack/cert-manager/releases/latest/download/cert-manager.yaml
	@echo "⏳ Waiting for cert-manager to be ready..."
	kubectl wait --namespace cert-manager \
	  --for=condition=ready pod \
	  --selector=app.kubernetes.io/instance=cert-manager \
	  --timeout=120s
	@echo "📜 Applying Let's Encrypt ClusterIssuer..."
	kubectl apply -f cert-manager/letsencrypt-prod-issuer.yaml

secrets:
	@echo "🔑 Creating and sealing secrets..."
	kubectl create secret generic fusionauth-secret \
	  --from-literal=POSTGRES_USER=fusion \
	  --from-literal=POSTGRES_PASSWORD=fusionpass \
	  --namespace=$(NAMESPACE) \
	  --dry-run=client -o yaml > secrets/secret.yaml
	kubeseal --controller-namespace=kube-system < secrets/secret.yaml > secrets/sealed-secret.yaml
	kubectl apply -f secrets/sealed-secret.yaml --namespace=$(NAMESPACE)
	@echo "✅ SealedSecret applied."

deploy:
	@echo "🚀 Deploying FusionAuth app via Kustomize..."
	kubectl apply -k overlays/prod/ --namespace=$(NAMESPACE)
	@echo "✅ Deployment complete."

clean:
	kubectl delete -k overlays/prod/ --namespace=$(NAMESPACE)
	kubectl delete sealedsecret fusionauth-secret --namespace=$(NAMESPACE)
	kubectl delete namespace $(NAMESPACE)
