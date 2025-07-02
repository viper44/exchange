# Exchange Rate API

A Spring Boot REST API that fetches real-time exchange rates and performs currency conversions.

**Requirements:** Java 21

## How to Run

```bash
# Run the application
mvn spring-boot:run
```

Application starts on `http://localhost:8080`

## How to Test

### Run Unit Tests
```bash
mvn test
```

### API Testing

**Swagger UI (Interactive):**
```
http://localhost:8080/swagger-ui.html
```

**Example API Calls:**
```bash
# Get exchange rate
curl "http://localhost:8080/api/v1/exchange-rates/USD/EUR"

# Get all rates from USD
curl "http://localhost:8080/api/v1/exchange-rates/USD"

# Convert currency
curl "http://localhost:8080/api/v1/conversions/USD/EUR?amount=100"

# Multi-currency conversion
curl -X POST "http://localhost:8080/api/v1/conversions/USD" \
  -H "Content-Type: application/json" \
  -d '{"amount": 100, "currencies": ["EUR", "GBP", "JPY"]}'
```

**Health Check:**
```bash
curl "http://localhost:8080/actuator/health"
```
