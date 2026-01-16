# Setting Up GitHub Secrets for Production Credentials

## 📋 Required GitHub Secrets

Navigate to your GitHub repository → Settings → Secrets and variables → Actions → New repository secret

### 1. Application Secrets

#### JWT Secrets
```
Name: JWT_SECRET
Value: your-super-secure-jwt-secret-key-minimum-256-bits
```

```
Name: STAGING_JWT_SECRET
Value: your-staging-jwt-secret-key-different-from-production
```

#### Database Credentials
```
Name: PROD_DB_URL
Value: jdbc:mysql://your-production-db-host:3306/kartol_production
```

```
Name: PROD_DB_USERNAME
Value: kartol_prod_user
```

```
Name: PROD_DB_PASSWORD
Value: your-secure-production-db-password
```

```
Name: STAGING_DB_URL
Value: jdbc:mysql://your-staging-db-host:3306/kartol_staging
```

```
Name: STAGING_DB_USERNAME
Value: kartol_staging_user
```

```
Name: STAGING_DB_PASSWORD
Value: your-staging-db-password
```

#### Email Configuration
```
Name: MAIL_HOST
Value: smtp.gmail.com
```

```
Name: MAIL_PORT
Value: 587
```

```
Name: MAIL_USERNAME
Value: your-email@gmail.com
```

```
Name: MAIL_PASSWORD
Value: your-app-specific-password
```

### 2. Container Registry Secrets (if using private registry)

```
Name: DOCKER_REGISTRY_USERNAME
Value: your-registry-username
```

```
Name: DOCKER_REGISTRY_PASSWORD
Value: your-registry-password
```

### 3. Kubernetes Secrets (if using K8s deployment)

```
Name: KUBE_CONFIG
Value: (base64-encoded kubeconfig file content)
```

To get the kubeconfig content:
```bash
cat ~/.kube/config | base64 -w 0
```

## 🔐 Security Best Practices

1. **Use strong, unique passwords** for each environment
2. **Rotate secrets regularly** (every 90 days recommended)
3. **Use environment-specific secrets** (never reuse between staging and production)
4. **Limit secret access** to only necessary team members
5. **Use GitHub's secret scanning** to detect leaked secrets

## 📝 Example Values (DO NOT USE IN PRODUCTION)

```
JWT_SECRET: MySuperSecretKey123456789012345678901234567890
STAGING_JWT_SECRET: StagingSecretKey098765432109876543210987654321
PROD_DB_PASSWORD: ProdDbP@ssw0rd!2024
STAGING_DB_PASSWORD: StagingDbP@ssw0rd!2024
MAIL_PASSWORD: abcd efgh ijkl mnop  # Gmail app password
```

## ✅ Verification

After setting up secrets, verify they're accessible in your workflow by adding a debug step:

```yaml
- name: Debug secrets
  run: |
    echo "JWT_SECRET is set: ${{ secrets.JWT_SECRET != '' }}"
    echo "PROD_DB_URL is set: ${{ secrets.PROD_DB_URL != '' }}"
```

⚠️ **Important**: Never echo actual secret values in logs!
