#!/bin/bash

# Deployment Script for KArtolApp
# Usage: ./deploy.sh [environment] [tag]

set -e

# Default values
ENVIRONMENT=${1:-staging}
TAG=${2:-latest}

echo "🚀 Starting deployment to $ENVIRONMENT environment with tag $TAG"

# Validate environment
if [[ ! "$ENVIRONMENT" =~ ^(staging|production)$ ]]; then
    echo "❌ Invalid environment. Use 'staging' or 'production'"
    exit 1
fi

# Set variables based on environment
if [ "$ENVIRONMENT" = "staging" ]; then
    NAMESPACE="kartol-staging"
    SERVICE_NAME="kartol-app-staging"
    INGRESS_HOST="staging.kartol.com"
else
    NAMESPACE="kartol-production"
    SERVICE_NAME="kartol-app-production"
    INGRESS_HOST="kartol.com"
fi

REGISTRY="ghcr.io"
IMAGE_NAME="$REGISTRY/$(git config --get remote.origin.url | sed 's/.*\///' | sed 's/.git$//'):$TAG"

echo "📦 Using image: $IMAGE_NAME"

# Check if kubectl is available
if ! command -v kubectl &> /dev/null; then
    echo "❌ kubectl is not installed or not in PATH"
    exit 1
fi

# Check if we're connected to the cluster
if ! kubectl cluster-info &> /dev/null; then
    echo "❌ Cannot connect to Kubernetes cluster"
    exit 1
fi

# Create namespace if it doesn't exist
echo "🔧 Ensuring namespace $NAMESPACE exists..."
kubectl create namespace $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -

# Deploy application
echo "🚀 Deploying application to $NAMESPACE..."

# Apply ConfigMaps
kubectl apply -f k8s/$ENVIRONMENT/configmap.yaml -n $NAMESPACE

# Apply Secrets
kubectl apply -f k8s/$ENVIRONMENT/secrets.yaml -n $NAMESPACE

# Apply Deployment
envsubst < k8s/$ENVIRONMENT/deployment.yaml | kubectl apply -f - -n $NAMESPACE

# Apply Service
kubectl apply -f k8s/$ENVIRONMENT/service.yaml -n $NAMESPACE

# Apply Ingress
kubectl apply -f k8s/$ENVIRONMENT/ingress.yaml -n $NAMESPACE

# Wait for deployment to be ready
echo "⏳ Waiting for deployment to be ready..."
kubectl rollout status deployment/$SERVICE_NAME -n $NAMESPACE --timeout=300s

# Get the status
echo "📊 Deployment status:"
kubectl get pods -n $NAMESPACE -l app=$SERVICE_NAME

# Get the external URL
echo "🌐 Application URL:"
if [ "$ENVIRONMENT" = "staging" ]; then
    echo "https://staging.kartol.com"
else
    echo "https://kartol.com"
fi

echo "✅ Deployment completed successfully!"

# Health check
echo "🏥 Performing health check..."
sleep 30

HEALTH_URL="https://$INGRESS_HOST/actuator/health"
if curl -f -s $HEALTH_URL > /dev/null; then
    echo "✅ Health check passed!"
else
    echo "⚠️  Health check failed. Please check the application logs."
    echo "📋 To check logs: kubectl logs -n $NAMESPACE -l app=$SERVICE_NAME --tail=50"
fi

echo "🎉 Deployment process completed!"
