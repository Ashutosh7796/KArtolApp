# CI/CD Pipeline Configuration for KArtolApp

This document describes the CI/CD pipeline setup for the KArtol Spring Boot JWT application.

## 🚀 Overview

The CI/CD pipeline is configured using GitHub Actions and includes:
- Automated testing with MySQL TestContainers
- Docker image building and pushing to GitHub Container Registry
- Security scanning with Trivy
- Multi-environment deployments (staging/production)
- Code coverage reporting with JaCoCo and Codecov

## 📁 Pipeline Structure

### GitHub Actions Workflow
- **Location**: `.github/workflows/ci-cd.yml`
- **Triggers**: Push to `main`/`develop` branches, pull requests to `main`

### Pipeline Jobs

#### 1. Test Job
- Runs on Ubuntu latest
- Sets up JDK 17
- Starts MySQL 8.0 service for integration tests
- Runs Maven tests with TestContainers
- Generates test reports and uploads to GitHub
- Uploads code coverage to Codecov

#### 2. Build and Push Job
- Runs only on `main` branch after successful tests
- Builds Docker image using multi-stage Dockerfile
- Pushes to GitHub Container Registry (ghcr.io)
- Uses semantic versioning for image tags

#### 3. Security Scan Job
- Runs Trivy vulnerability scanner
- Uploads results to GitHub Security tab
- Identifies security vulnerabilities in dependencies

#### 4. Deploy Jobs
- **Staging**: Deploys to staging environment on `develop` branch
- **Production**: Deploys to production environment on `main` branch

## 🐳 Docker Configuration

### Dockerfile Features
- Multi-stage build (Maven build + OpenJDK runtime)
- Non-root user execution
- Health checks using Spring Actuator
- Optimized JVM settings for containers
- Security best practices

### Docker Compose
- Complete local development setup
- MySQL 8.0 database with persistent storage
- Nginx reverse proxy configuration
- Health checks for all services
- Proper networking and volume management

## 🔧 Environment Configuration

### Application Profiles
- `application-docker.properties`: Docker environment
- `application-staging.properties`: Staging environment
- `application-dev.properties`: Development environment
- `application-pord.properties`: Production environment (typo in original)

### Environment Variables
Key environment variables for different environments:

#### Database
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

#### Security
- `JWT_SECRET`
- `STAGING_JWT_SECRET`

#### Email
- `MAIL_HOST`, `MAIL_PORT`
- `MAIL_USERNAME`, `MAIL_PASSWORD`

## 🚀 Deployment

### Local Development
```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down
```

### Manual Deployment
```bash
# Make deploy script executable
chmod +x scripts/deploy.sh

# Deploy to staging
./scripts/deploy.sh staging latest

# Deploy to production
./scripts/deploy.sh production v1.0.0
```

### Kubernetes Deployment
The pipeline supports Kubernetes deployment with the following structure:
```
k8s/
├── staging/
│   ├── configmap.yaml
│   ├── secrets.yaml
│   ├── deployment.yaml
│   ├── service.yaml
│   └── ingress.yaml
└── production/
    ├── configmap.yaml
    ├── secrets.yaml
    ├── deployment.yaml
    ├── service.yaml
    └── ingress.yaml
```

## 🔍 Monitoring and Health Checks

### Spring Actuator Endpoints
- `/actuator/health`: Application health status
- `/actuator/info`: Application information
- `/actuator/metrics`: Application metrics
- `/actuator/prometheus`: Prometheus metrics

### Health Check Configuration
- Docker container health checks
- Kubernetes liveness and readiness probes
- Application health monitoring

## 🛡️ Security Features

### Pipeline Security
- Trivy vulnerability scanning
- Dependency vulnerability detection
- Security reporting to GitHub Security tab
- Non-root Docker container execution

### Application Security
- JWT authentication
- Rate limiting with Resilience4j
- Password encryption with Jasypt
- SSL/TLS configuration support

## 📊 Code Quality and Coverage

### JaCoCo Configuration
- Minimum 50% line coverage requirement
- Coverage reports generated on each test run
- Integration with Codecov for coverage tracking

### Test Configuration
- Unit tests with JUnit 5 and Mockito
- Integration tests with TestContainers
- API testing with Postman collections
- Test reporting in GitHub Actions

## 🔧 Required Secrets

Configure these secrets in your GitHub repository:

#### For CI/CD Pipeline
- `GITHUB_TOKEN`: Automatically provided by GitHub Actions

#### For Deployment (if using Kubernetes)
- `KUBE_CONFIG`: Kubernetes configuration file
- `DOCKER_REGISTRY_USERNAME`: Container registry username
- `DOCKER_REGISTRY_PASSWORD`: Container registry password

#### For Application
- `JWT_SECRET`: JWT signing secret
- `STAGING_JWT_SECRET`: Staging JWT secret
- `DB_PASSWORD_*`: Database passwords for different environments
- `MAIL_*`: Email configuration credentials

## 🚨 Troubleshooting

### Common Issues

#### Test Failures
- Check MySQL service startup in logs
- Verify TestContainers configuration
- Review test database connection settings

#### Build Failures
- Verify Java 17 is properly configured
- Check Maven dependency resolution
- Review Docker build logs for layer issues

#### Deployment Failures
- Verify Kubernetes cluster connectivity
- Check namespace and RBAC configuration
- Review application logs for startup errors

### Debugging Commands
```bash
# Check GitHub Actions logs
# Navigate to Actions tab in GitHub repository

# Check Docker container logs
docker logs <container-name>

# Check Kubernetes pod logs
kubectl logs -n <namespace> <pod-name>

# Check deployment status
kubectl rollout status deployment/<deployment-name> -n <namespace>
```

## 📝 Best Practices

1. **Branch Strategy**: Use `main` for production, `develop` for staging
2. **Semantic Versioning**: Use proper version tags for releases
3. **Environment Variables**: Use environment-specific configurations
4. **Security**: Regularly update dependencies and scan for vulnerabilities
5. **Monitoring**: Set up proper alerts and monitoring in production
6. **Backups**: Regularly backup database and application data

## 🔄 Continuous Improvement

The CI/CD pipeline is designed to be:
- **Automated**: Minimal manual intervention required
- **Scalable**: Easy to add new environments or services
- **Secure**: Built-in security scanning and best practices
- **Observable**: Comprehensive logging and monitoring
- **Maintainable**: Clear documentation and modular configuration
