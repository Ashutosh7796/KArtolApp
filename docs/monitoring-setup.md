# Monitoring and Alerting Setup Guide

## 📊 Overview

This guide covers setting up comprehensive monitoring and alerting for KArtolApp using Prometheus, Grafana, and AlertManager.

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   KArtolApp     │    │   Prometheus    │    │  AlertManager   │
│ (Staging/Prod)  │───▶│   (Metrics)     │───▶│   (Alerts)      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │                        │
                              ▼                        ▼
                       ┌─────────────────┐    ┌─────────────────┐
                       │     Grafana     │    │   Email/Slack   │
                       │ (Visualization)│    │  (Notifications)│
                       └─────────────────┘    └─────────────────┘
```

## 🚀 Quick Setup

### 1. Deploy Monitoring Stack

```bash
# Deploy all monitoring components
kubectl apply -f monitoring/k8s/monitoring-stack.yaml

# Check deployment status
kubectl get pods -n monitoring
kubectl get services -n monitoring
```

### 2. Access Grafana

```bash
# Get Grafana URL
kubectl get service grafana -n monitoring

# If using LoadBalancer
kubectl port-forward -n monitoring svc/grafana 3000:3000

# Access at: http://localhost:3000
# Username: admin
# Password: admin123
```

### 3. Import Dashboard

1. Login to Grafana
2. Go to Dashboards → Import
3. Upload `monitoring/grafana/dashboard.json`
4. Select Prometheus as data source

## 🔧 Detailed Configuration

### Prometheus Configuration

#### Key Features
- **Service Discovery**: Automatically discovers KArtolApp services
- **Multi-environment**: Monitors both staging and production
- **Kubernetes Integration**: Monitors cluster health
- **Custom Metrics**: Application-specific metrics

#### Configuration Files
- `monitoring/prometheus.yml`: Main configuration
- `monitoring/alert_rules.yml`: Alerting rules
- `monitoring/k8s/monitoring-stack.yaml`: Kubernetes deployment

### AlertManager Configuration

#### Alert Routing
- **Critical Alerts**: Immediate notification (oncall, devops)
- **Warning Alerts**: Standard notification (devops, dev-team)
- **Info Alerts**: Low priority (dev-team only)

#### Notification Channels
- **Email**: SMTP configuration
- **Slack**: Webhook integration
- **PagerDuty**: Emergency escalation

#### Configuration Files
- `monitoring/alertmanager.yml`: Alert routing and notifications

### Grafana Dashboard

#### Included Panels
1. **Application Status**: Up/down status
2. **Request Rate**: HTTP requests per second
3. **Response Time**: 50th, 95th, 99th percentiles
4. **Error Rate**: 4xx and 5xx errors
5. **JVM Memory**: Heap and non-heap usage
6. **CPU Usage**: Process CPU utilization
7. **Database Pool**: Connection pool metrics
8. **Garbage Collection**: GC time and frequency
9. **Authentication**: Login success/failure rates
10. **Rate Limiting**: Rate limit violations

## 📱 Setting Up Notifications

### Email Configuration

Update `monitoring/alertmanager.yml`:

```yaml
global:
  smtp_smarthost: 'smtp.gmail.com:587'
  smtp_from: 'alerts@kartol.com'
  smtp_auth_username: 'alerts@kartol.com'
  smtp_auth_password: 'your-app-password'
```

### Slack Integration

1. Create a Slack webhook URL
2. Update alertmanager configuration:

```yaml
slack_configs:
  - api_url: 'YOUR_SLACK_WEBHOOK_URL'
    channel: '#alerts-critical'
    title: '🚨 Critical Alert: {{ .GroupLabels.alertname }}'
```

### PagerDuty Integration

1. Get PagerDuty integration key
2. Update alertmanager configuration:

```yaml
webhook_configs:
  - url: 'https://api.pagerduty.com/integrations/v2/enqueue/YOUR_INTEGRATION_KEY'
```

## 🔍 Monitoring Metrics

### Application Metrics

#### HTTP Metrics
- `http_requests_total`: Total HTTP requests
- `http_request_duration_seconds`: Request duration histogram
- `http_requests_rate`: Request rate per second

#### JVM Metrics
- `jvm_memory_used_bytes`: Memory usage
- `jvm_gc_collection_seconds`: Garbage collection time
- `process_cpu_seconds_total`: CPU usage

#### Database Metrics
- `hikaricp_connections_active`: Active connections
- `hikaricp_connections_idle`: Idle connections
- `hikaricp_connections_timeout_total`: Connection timeouts

#### Security Metrics
- `authentication_success_total`: Successful logins
- `authentication_failure_total`: Failed logins
- `rate_limit_exceeded_total`: Rate limit violations

### Kubernetes Metrics

#### Pod Metrics
- `kube_pod_status_ready`: Pod readiness
- `kube_pod_container_status_restarts_total`: Pod restarts
- `container_memory_usage_bytes`: Container memory
- `container_cpu_usage_seconds_total`: Container CPU

## 🚨 Alert Rules

### Critical Alerts
- **ApplicationDown**: Application is unreachable
- **DatabaseConnectionPoolExhausted**: Database connections exhausted
- **PodCrashLooping**: Pod is continuously restarting

### Warning Alerts
- **ApplicationHighErrorRate**: High 5xx error rate
- **HighMemoryUsage**: Memory usage above 80%
- **HighCPUUsage**: CPU usage above 80%

### Info Alerts
- **LowUserActivity**: Unusually low user activity

## 📊 Custom Metrics

### Adding Custom Metrics

Add to your Spring Boot application:

```java
// Counter for custom events
private final Counter customEventCounter;

// Gauge for current values
private final Gauge currentValueGauge;

// Timer for operation duration
private final Timer operationTimer;
```

### Exposing Custom Metrics

Add to `application.properties`:

```properties
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.metrics.export.prometheus.enabled=true
```

## 🔧 Maintenance

### Scaling Prometheus

```yaml
resources:
  requests:
    memory: "2Gi"
    cpu: "1000m"
  limits:
    memory: "4Gi"
    cpu: "2000m"
```

### Data Retention

```yaml
# In prometheus.yml
--storage.tsdb.retention.time=30d
```

### Backup Configuration

```bash
# Backup Prometheus data
kubectl exec -n monitoring prometheus-0 -- tar czf - /prometheus/ > prometheus-backup.tar.gz

# Backup Grafana dashboards
kubectl exec -n monitoring grafana-0 -- tar czf - /var/lib/grafana/ > grafana-backup.tar.gz
```

## 🧪 Testing Alerts

### Test Alert Rules

```bash
# Access Prometheus UI
kubectl port-forward -n monitoring svc/prometheus 9090:9090

# Go to http://localhost:9090/alerts
# Check active alerts and rules
```

### Test Notifications

1. Trigger a test alert:
```bash
# Simulate application down
kubectl scale deployment kartol-app-production --replicas=0 -n kartol-production
```

2. Check AlertManager UI:
```bash
kubectl port-forward -n monitoring svc/alertmanager 9093:9093
# Go to http://localhost:9093
```

## 📈 Performance Optimization

### Prometheus Optimization

1. **Reduce Scrape Interval** for high-frequency metrics
2. **Use Recording Rules** for expensive queries
3. **Implement Federation** for multiple clusters

### Grafana Optimization

1. **Use Variables** for dynamic dashboards
2. **Implement Annotations** for events
3. **Use Dashboard Links** for navigation

## 🔐 Security Considerations

### Network Security
```yaml
# Network policies for monitoring
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: monitoring-netpol
  namespace: monitoring
spec:
  podSelector: {}
  policyTypes:
  - Ingress
  - Egress
```

### Authentication
- Enable Grafana authentication
- Use OAuth for Grafana
- Implement RBAC for Kubernetes

## 📋 Monitoring Checklist

- [ ] Deploy monitoring stack
- [ ] Configure notification channels
- [ ] Import Grafana dashboards
- [ ] Test alert rules
- [ ] Set up backup procedures
- [ ] Document escalation procedures
- [ ] Configure retention policies
- [ ] Set up monitoring for monitoring

## 🆘 Troubleshooting

### Common Issues

#### Prometheus Not Scraping
```bash
# Check Prometheus logs
kubectl logs -n monitoring prometheus-0

# Check target status
kubectl port-forward -n monitoring svc/prometheus 9090:9090
# Visit http://localhost:9090/targets
```

#### Alerts Not Firing
```bash
# Check alert rules
curl http://localhost:9090/api/v1/rules

# Check AlertManager configuration
kubectl logs -n monitoring alertmanager-0
```

#### Grafana Not Connecting
```bash
# Check Grafana logs
kubectl logs -n monitoring grafana-0

# Test Prometheus data source
# In Grafana: Configuration → Data Sources → Test
```

## 📚 Additional Resources

- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)
- [AlertManager Documentation](https://prometheus.io/docs/alerting/latest/alertmanager/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
