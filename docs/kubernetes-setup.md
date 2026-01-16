# Kubernetes Setup Guide

## 🚀 Prerequisites

1. **Kubernetes Cluster** (v1.20+)
2. **kubectl** configured and connected to your cluster
3. **Helm** (optional, for additional services)
4. **Domain name** for Ingress configuration

## 📋 Setup Steps

### 1. Create Namespaces

```bash
kubectl apply -f k8s/namespaces.yaml
```

### 2. Create Image Pull Secret

```bash
kubectl create secret docker-registry ghcr-secret \
  --docker-server=ghcr.io \
  --docker-username=YOUR_GITHUB_USERNAME \
  --docker-password=YOUR_GITHUB_TOKEN \
  --namespace=kartol-staging

kubectl create secret docker-registry ghcr-secret \
  --docker-server=ghcr.io \
  --docker-username=YOUR_GITHUB_USERNAME \
  --docker-password=YOUR_GITHUB_TOKEN \
  --namespace=kartol-production
```

### 3. Configure Secrets

Edit the secret files and replace the placeholder values:

```bash
# Encode your secrets
echo -n "your-staging-db-password" | base64
echo -n "your-production-db-password" | base64
echo -n "your-jwt-secret" | base64
echo -n "your-email@gmail.com" | base64
echo -n "your-app-password" | base64
```

Update the secret files:
- `k8s/staging/secrets.yaml`
- `k8s/production/secrets.yaml`

### 4. Install NGINX Ingress Controller

```bash
# Using Helm
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx
helm repo update

helm install ingress-nginx ingress-nginx/ingress-nginx \
  --namespace ingress-nginx --create-namespace
```

### 5. Install Cert-Manager (for SSL certificates)

```bash
# Install cert-manager
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.13.0/cert-manager.yaml

# Create cluster issuers
kubectl apply -f k8s/cert-manager/cluster-issuers.yaml
```

### 6. Deploy to Staging

```bash
# Apply all staging manifests
kubectl apply -f k8s/staging/ -n kartol-staging

# Check deployment status
kubectl get pods -n kartol-staging
kubectl get ingress -n kartol-staging
```

### 7. Deploy to Production

```bash
# Apply all production manifests
kubectl apply -f k8s/production/ -n kartol-production

# Check deployment status
kubectl get pods -n kartol-production
kubectl get ingress -n kartol-production
```

## 🔧 Configuration

### Environment Variables

The manifests use environment variables that should be set in your CI/CD pipeline:

```bash
export IMAGE_NAME="ghcr.io/your-username/kartolapp"
export TAG="latest"
export STAGING_DB_URL="jdbc:mysql://staging-db:3306/kartol_staging"
export PROD_DB_URL="jdbc:mysql://prod-db:3306/kartol_production"
```

### Ingress Configuration

Update the hostnames in the ingress files:
- `staging.kartol.com` → your staging domain
- `kartol.com` → your production domain

## 🔍 Verification

### Check Pod Status
```bash
kubectl get pods -n kartol-staging
kubectl get pods -n kartol-production
```

### Check Services
```bash
kubectl get services -n kartol-staging
kubectl get services -n kartol-production
```

### Check Ingress
```bash
kubectl get ingress -n kartol-staging
kubectl get ingress -n kartol-production
```

### Test Application Health
```bash
# Staging
kubectl port-forward -n kartol-staging svc/kartol-app-staging-service 8080:80
curl http://localhost:8080/actuator/health

# Production
kubectl port-forward -n kartol-production svc/kartol-app-production-service 8081:80
curl http://localhost:8081/actuator/health
```

## 🚨 Troubleshooting

### Common Issues

1. **Image Pull Errors**
   ```bash
   kubectl describe pod <pod-name> -n <namespace>
   # Check if image pull secret is correctly configured
   ```

2. **Pod Crashing**
   ```bash
   kubectl logs <pod-name> -n <namespace>
   kubectl logs <pod-name> -n <namespace> --previous
   ```

3. **Ingress Not Working**
   ```bash
   kubectl describe ingress <ingress-name> -n <namespace>
   # Check NGINX controller logs
   kubectl logs -n ingress-nginx -l app.kubernetes.io/component=controller
   ```

4. **Database Connection Issues**
   ```bash
   # Check if database is accessible from the pod
   kubectl exec -it <pod-name> -n <namespace> -- nc -zv <db-host> 3306
   ```

### Debug Commands

```bash
# Get detailed pod information
kubectl describe pod <pod-name> -n <namespace>

# Get events
kubectl get events -n <namespace> --sort-by=.metadata.creationTimestamp

# Port forward for local testing
kubectl port-forward -n <namespace> svc/<service-name> <local-port>:<service-port>

# Execute into pod
kubectl exec -it <pod-name> -n <namespace> -- /bin/bash
```

## 📊 Monitoring Setup

### Prometheus Integration

The production service includes Prometheus annotations for metrics collection:

```yaml
annotations:
  prometheus.io/scrape: "true"
  prometheus.io/port: "8080"
  prometheus.io/path: "/actuator/prometheus"
```

### Grafana Dashboard

Import the Spring Boot dashboard to monitor:
- JVM metrics
- HTTP request metrics
- Database connection pool
- Custom application metrics

## 🔐 Security Considerations

1. **Network Policies**: Consider adding network policies to restrict traffic
2. **Pod Security Policies**: Use security contexts and non-root users
3. **RBAC**: Implement proper role-based access control
4. **Secrets Management**: Use external secret stores like HashiCorp Vault
5. **Image Scanning**: Scan images for vulnerabilities before deployment

## 🔄 Updates and Maintenance

### Rolling Updates

The production deployment is configured for rolling updates with zero downtime:

```yaml
strategy:
  type: RollingUpdate
  rollingUpdate:
    maxSurge: 1
    maxUnavailable: 0
```

### Backup Strategy

1. **Database Backups**: Regular MySQL backups
2. **Configuration Backups**: Version control all manifests
3. **Disaster Recovery**: Document recovery procedures
