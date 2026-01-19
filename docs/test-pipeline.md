# Testing the CI/CD Pipeline

## 🚀 Step-by-Step Pipeline Testing

### 1. Prepare Your Repository

Ensure you have:
- ✅ GitHub repository created
- ✅ All CI/CD files committed
- ✅ GitHub Secrets configured (from setup-github-secrets.md)

### 2. Test Pipeline by Pushing to Develop Branch

#### Step 2.1: Switch to Develop Branch
```bash
# If you don't have a develop branch yet
git checkout -b develop
git push -u origin develop

# If develop branch already exists
git checkout develop
git pull origin develop
```

#### Step 2.2: Make a Small Change
```bash
# Make a test change to trigger the pipeline
echo "# Pipeline Test $(date)" >> test-pipeline.txt
git add test-pipeline.txt
git commit -m "test: trigger CI/CD pipeline for develop branch"
git push origin develop
```

#### Step 2.3: Monitor the Pipeline
1. Go to your GitHub repository
2. Click on **Actions** tab
3. You should see the workflow running
4. Monitor each job:
   - **test**: Should run and pass
   - **build-and-push**: Should build and push Docker image
   - **security-scan**: Should scan for vulnerabilities
   - **deploy-staging**: Should attempt deployment (may fail if K8s not set up)

### 3. Expected Pipeline Behavior

#### Test Job ✅
- Sets up JDK 17
- Starts MySQL container
- Runs Maven tests
- Generates test reports
- Uploads coverage to Codecov

#### Build and Push Job ✅
- Builds Docker image
- Pushes to GitHub Container Registry (ghcr.io)
- Tags image appropriately

#### Security Scan Job ✅
- Runs Trivy vulnerability scanner
- Uploads results to GitHub Security tab

#### Deploy Staging Job ⚠️
- Will run but may fail if Kubernetes cluster not configured
- This is expected if you haven't set up K8s yet

### 4. Troubleshooting Common Issues

#### Pipeline Doesn't Start
```bash
# Check if .github/workflows directory exists
ls -la .github/workflows/

# Verify workflow file syntax
cat .github/workflows/ci-cd.yml
```

#### Test Job Fails
1. **Check MySQL service startup** in the logs
2. **Verify TestContainers configuration**
3. **Check database connection settings**

#### Build Job Fails
1. **Check Docker build logs**
2. **Verify Dockerfile syntax**
3. **Check registry permissions**

#### Security Scan Fails
1. **Check Trivy installation**
2. **Verify file permissions**

### 5. Test Production Pipeline

#### Step 5.1: Merge to Main
```bash
# Switch to main branch
git checkout main
git pull origin main

# Merge develop into main
git merge develop
git push origin main
```

#### Step 5.2: Monitor Production Pipeline
- Same workflow will run for main branch
- **deploy-production** job will execute instead of staging

### 6. Verification Commands

#### Check GitHub Container Registry
```bash
# List images in your registry
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     https://api.github.com/user/packages

# Check specific image tags
curl -H "Authorization: Bearer $GITHUB_TOKEN" \
     https://api.github.com/packages/container/YOUR_USERNAME/kartolapp/versions
```

#### Test Docker Image Locally
```bash
# Pull the image
docker pull ghcr.io/YOUR_USERNAME/kartolapp:latest

# Run it locally
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=docker \
  ghcr.io/YOUR_USERNAME/kartolapp:latest

# Test health endpoint
curl http://localhost:8080/actuator/health
```

### 7. Pipeline Success Indicators

#### ✅ Successful Test Job
- All tests pass
- Code coverage uploaded
- Test reports generated

#### ✅ Successful Build Job
- Docker image built
- Image pushed to registry
- Proper tagging applied

#### ✅ Successful Security Scan
- No critical vulnerabilities
- SARIF report uploaded
- Security tab updated

#### ✅ Successful Deployment (if K8s configured)
- Pods running
- Services accessible
- Ingress working

### 8. Performance Monitoring

#### Check Pipeline Duration
```bash
# In GitHub Actions UI, check:
# - Total workflow time
# - Individual job times
# - Resource utilization
```

#### Optimize Pipeline
- **Cache Maven dependencies** (already configured)
- **Use Docker layer caching** (already configured)
- **Parallelize jobs** where possible

### 9. Next Steps After Successful Test

1. **Set up Kubernetes cluster** if not already done
2. **Configure monitoring** (see monitoring setup guide)
3. **Set up alerts** for pipeline failures
4. **Document team procedures**
5. **Schedule regular pipeline maintenance**

### 10. Rollback Procedures

If pipeline fails:
```bash
# Rollback to previous commit
git revert HEAD
git push origin develop

# Or reset to known good commit
git reset --hard <commit-hash>
git push --force-with-lease origin develop
```

### 📝 Quick Test Checklist

- [ ] GitHub repository created
- [ ] Workflow files committed
- [ ] GitHub Secrets configured
- [ ] Develop branch exists
- [ ] Test change pushed
- [ ] Pipeline starts automatically
- [ ] Test job passes
- [ ] Build job succeeds
- [ ] Security scan completes
- [ ] Docker image in registry
- [ ] Deployment job runs (may fail initially)

### 🎯 Success Criteria

Your pipeline is working when:
1. ✅ All jobs execute automatically on push
2. ✅ Tests pass and coverage is reported
3. ✅ Docker image is built and pushed
4. ✅ Security scan completes
5. ✅ Deployment attempts (even if it fails initially)

Once you achieve these, your CI/CD pipeline is fully functional!
