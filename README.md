# OneBhoomi API Gateway

The API Gateway is the entry point for all client requests to the OneBhoomi platform. It handles request routing, authentication, rate limiting, and provides circuit breaking for resilience.

## Features

- **Dynamic Routing**: Forwards requests to appropriate microservices
- **Authentication**: Validates JWT tokens and adds user context for downstream services
- **Rate Limiting**: Prevents abuse with Redis-backed rate limiting
- **Circuit Breaking**: Handles service failures with circuit breakers
- **Request/Response Transformation**: Modifies requests and responses as needed
- **Monitoring**: Provides detailed metrics and health checks

## Architecture

The API Gateway sits between clients and the OneBhoomi microservices:

```
Clients → API Gateway → Microservices
```

The gateway handles routing to the following services:
- Auth Service
- Cloud Adapters
- Deployment Service
- Identity Service
- Configuration Service
- Compliance Service
- Analytics Service

## Setup and Configuration

### Prerequisites

- Java 17 or higher
- Maven 3.8.x or higher
- Redis (for rate limiting)
- Eureka Service Registry (for service discovery)

### Local Development

1. Clone the repository:
   ```bash
   git clone https://github.com/onebhoomi/onebhoomi-api-gateway.git
   cd onebhoomi-api-gateway
   ```

2. Configure application properties:
   
   Update `src/main/resources/application.yml` with your environment-specific settings, or use the default configuration for local development.

3. Build the application:
   ```bash
   ./mvnw clean package
   ```

4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

   The API Gateway will be accessible at http://localhost:8080.

### Environment Variables

The following environment variables can be configured:

| Variable | Description | Default |
|----------|-------------|---------|
| `REDIS_HOST` | Redis server hostname | localhost |
| `REDIS_PORT` | Redis server port | 6379 |
| `EUREKA_HOST` | Eureka server hostname | localhost |
| `EUREKA_PORT` | Eureka server port | 8761 |
| `JWT_SECRET` | Secret key for JWT validation | changeme |
| `JWT_EXPIRATION` | JWT token expiration in seconds | 86400 |
| `ALLOWED_ORIGINS` | CORS allowed origins | * |
| `ALLOWED_METHODS` | CORS allowed methods | GET,POST,PUT,DELETE,OPTIONS |
| `ALLOWED_HEADERS` | CORS allowed headers | * |

### Profiles

The application supports the following Spring profiles:

- **dev**: Development environment settings
- **prod**: Production environment settings

To run with a specific profile:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## API Routes

All API requests are routed through the gateway with the following base paths:

| Service | Base Path |
|---------|-----------|
| Auth Service | `/api/v1/auth/**` |
| Cloud Adapters | `/api/v1/cloud/**` |
| Deployment Service | `/api/v1/deployments/**` |
| Identity Service | `/api/v1/identity/**` |
| Configuration Service | `/api/v1/configurations/**` |
| Compliance Service | `/api/v1/compliance/**` |
| Analytics Service | `/api/v1/analytics/**` |

## Authentication

The API Gateway validates JWT tokens for protected endpoints. Add an `Authorization` header with a Bearer token to authenticate requests:

```
Authorization: Bearer <token>
```

Public endpoints that don't require authentication:
- `/api/v1/auth/login`
- `/api/v1/auth/register`
- `/api/v1/health/**`
- `/actuator/**`

## Rate Limiting

Rate limiting is configured for all endpoints to prevent abuse. Default settings:
- 10 requests per second replenish rate
- 20 requests burst capacity

Rate limiting uses Redis to maintain rate limits across gateway instances.

## Circuit Breaking

The API Gateway implements circuit breaking for all service routes. If a service is unresponsive or failing, the circuit will open and requests will be redirected to fallback endpoints.

Fallback endpoints provide graceful degradation for all services.

## Monitoring and Health Checks

Health endpoints are available to monitor the gateway:

- `/actuator/health`: Overall health status
- `/actuator/health/liveness`: Liveness check
- `/actuator/health/readiness`: Readiness check
- `/actuator/metrics`: Metrics information
- `/actuator/prometheus`: Prometheus metrics

## Building and Deployment

### Docker Build

```bash
docker build -t onebhoomi/api-gateway:latest .
```

### Kubernetes Deployment

Apply the Kubernetes configuration:

```bash
kubectl apply -f kubernetes/deployment.yaml
kubectl apply -f kubernetes/configmap.yaml
kubectl apply -f kubernetes/secrets.yaml
```

## Development

### Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── onebhoomi/
│   │           └── gateway/
│   │               ├── OneBhoomiGatewayApplication.java
│   │               ├── config/
│   │               │   ├── RouteConfig.java
│   │               │   ├── SecurityConfig.java
│   │               │   ├── RateLimitingConfig.java
│   │               │   ├── CircuitBreakerConfig.java
│   │               │   └── ActuatorConfig.java
│   │               ├── controller/
│   │               │   └── FallbackController.java
│   │               ├── filter/
│   │               │   └── AuthenticationFilter.java
│   │               ├── service/
│   │               │   └── JwtTokenValidator.java
│   │               └── exception/
│   │                   └── GlobalExceptionHandler.java
│   └── resources/
│       ├── application.yml
│       ├── application-dev.yml
│       └── application-prod.yml
└── test/
    └── java/
        └── com/
            └── onebhoomi/
                └── gateway/
                    ├── OneBhoomiGatewayApplicationTests.java
                    ├── filter/
                    │   └── AuthenticationFilterTest.java
                    ├── service/
                    │   └── JwtTokenValidatorTest.java
                    └── controller/
                        └── FallbackControllerTest.java
```

### Testing

Run unit tests:
```bash
./mvnw test
```

Run integration tests:
```bash
./mvnw verify
```

## Troubleshooting

### Common Issues

1. **Gateway not routing requests**
   - Check if Eureka service is running
   - Verify service instances are registered
   - Check route configuration

2. **Authentication failures**
   - Verify JWT token format and validity
   - Check if the secret key is correctly configured
   - Ensure token is not expired

3. **Rate limiting too strict**
   - Adjust rate limiting parameters in the configuration
   - Check Redis connection and functionality

4. **Circuit breaker triggering too frequently**
   - Check downstream service health
   - Adjust circuit breaker parameters

### Logs

Enable debug logging to diagnose issues:

```yaml
logging:
  level:
    org.springframework.cloud.gateway: DEBUG
    org.springframework.security: DEBUG
    com.onebhoomi.gateway: DEBUG
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.
