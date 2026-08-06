# Airbnb WhatsApp - Guest Registration System

A modern Spring Boot 3 application that manages guest registrations via WhatsApp using the Evolution API, with an administrative dashboard.

## 🎯 Features

- **WhatsApp Integration:** Receive and process guest registration forms via WhatsApp using Evolution API
- **State Machine Conversation Flow:** 10-step form completion with validation
- **Admin Dashboard:** Modern UI with Thymeleaf + Bootstrap 5
- **Persistence:** PostgreSQL with Flyway migrations and JPA
- **Clean Architecture:** Following SOLID principles and hexagonal architecture
- **Spring Boot Best Practices:** Global exception handling, validation, monitoring, profiles

## 🏗️ Architecture

This project follows **Clean Architecture** with:
- **Domain Layer:** Core business entities and rules
- **Application Layer:** Use cases and orchestration
- **Adapter Layer:** Controllers, repositories, external integrations
- **Config Layer:** Spring configuration and global handlers

For detailed architecture analysis, see [ARCHITECTURE_REVIEW.md](./ARCHITECTURE_REVIEW.md)

## 🚀 Quick Start

### Prerequisites
- Java 21+
- PostgreSQL 12+
- Maven 3.8+

### Setup

1. Clone and install:
```bash
git clone <repo>
cd airbnb-whatsapp
mvn clean install
```

2. Configure database (application.yml):
```yaml
spring:
  datasource:
	url: jdbc:postgresql://localhost:5432/airbnb
	username: postgres
	password: postgres
```

3. Configure Evolution API:
```bash
export EVOLUTION_API_KEY=your_api_key
```

4. Run:
```bash
mvn spring-boot:run
```

5. Access:
- Health: http://localhost:8080/health
- Admin: http://localhost:8080/admin/dashboard
- Webhook: POST http://localhost:8080/api/webhook/evolution

## 📁 Project Structure

```
src/main/java/com/acme/airbnbwhatsapp/
├── AirbnbWhatsappApplication.java      # Startup
├── adapters/                           # Ports & Adapters Layer
│   ├── in/web/                         # Inbound adapters (Controllers)
│   └── out/                            # Outbound adapters (External services)
├── application/                        # Application Layer (DTOs, mappers)
├── config/                             # Spring configuration
├── domain/                             # Domain Layer (Entities, enums)
└── service/                            # Services & State Machine
```

## 🔄 Conversation Flow

The WhatsApp registration follows this state machine:

1. **INICIO** → Greeting
2. **APARTAMENTO** → Ask for apartment number
3. **DATA_ENTRADA** → Check-in date
4. **DATA_SAIDA** → Check-out date
5. **RESPONSAVEL** → Responsible person
6. **QTD_HOSPEDES** → Number of guests
7. **NOME_HOSPEDE** → Guest name
8. **PLACA** → Vehicle plate
9. **OBSERVACAO** → Observations
10. **FINALIZADO** → Confirmation & summary

Each state validates input, persists data, and determines the next state.

## 🛠️ Technology Stack

| Layer | Technology |
|-------|-----------|
| **Runtime** | Java 21 |
| **Framework** | Spring Boot 3.2.0 |
| **Web** | Spring MVC |
| **Data** | Spring Data JPA + PostgreSQL |
| **Frontend** | Thymeleaf + Bootstrap 5 |
| **External API** | Spring WebClient (Evolution) |
| **Validation** | Jakarta Bean Validation |
| **Build** | Maven |
| **DB Migration** | Flyway |
| **Documentation** | Springdoc OpenAPI (Swagger) |

## 📊 Database Schema

- **hospede** → Guest information
- **hospedagem** → Booking/stay records
- **sessao_whatsapp** → WhatsApp conversation sessions

Migrations managed by Flyway in `src/main/resources/db/migration/`

## 🔌 API Endpoints

### Webhook (REST)
```
POST /api/webhook/evolution
Content-Type: application/json

{
  "externalId": "msg-123",
  "from": "+5511999999999",
  "text": "apartamento 501"
}
```

### Health (REST)
```
GET /health
→ { "status": "UP", "timestamp": "...", "application": "airbnb-whatsapp" }
```

### Admin (HTML)
```
GET /admin/dashboard         → Dashboard with stats
GET /admin/hospedagens       → List with filters & pagination
GET /admin/hospedagens/{id}  → Details view
```

## 🔐 Configuration

### Profiles

**Development (default):**
```bash
mvn spring-boot:run
```

**Production:**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

Environment variables (prod):
- `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`
- `EVOLUTION_API_KEY`

## ✅ SOLID Principles Implementation

| Principle | Implementation |
|-----------|----------------|
| **S**ingle Responsibility | OutboundMessagingService, each state handler |
| **O**pen/Closed | Generic sendMessage() with switch for extensibility |
| **L**iskov Substitution | StateHandler interface, MessagingPort interface |
| **I**nterface Segregation | Separate concerns via MessagingPort |
| **D**ependency Inversion | MessagingPort abstraction, service-level dependencies |

## 🧪 Testing (Recommended)

```bash
# Unit tests
mvn test

# Integration tests with Testcontainers
mvn verify
```

## 📖 Documentation

- **Architecture Review:** [ARCHITECTURE_REVIEW.md](./ARCHITECTURE_REVIEW.md)
- **API Docs (Swagger):** http://localhost:8080/swagger-ui.html
- **Actuator Endpoints:** http://localhost:8080/actuator

## 🐳 Docker Deployment (Optional)

```bash
# Build and run with Docker Compose
docker-compose up -d
```

## 📝 Logging

Logs are configured in `application.yml`:
- **Development:** Console output with DEBUG level for application
- **Production:** File-based with rotation (logs/airbnb-whatsapp.log)

## 🚨 Error Handling

Global exception handler provides consistent error responses:
```json
{
  "timestamp": "2026-08-04T12:00:00Z",
  "status": 400,
  "error": "Validation Failed",
  "errors": {
	"from": "from (phone number) must not be blank"
  },
  "path": "/api/webhook/evolution"
}
```

## 📈 Monitoring

Spring Boot Actuator endpoints available:
- `/health` → Application health status
- `/info` → Application info
- `/metrics` → Metrics endpoint

Configure in `application.yml` under `management.*`

## 🔮 Future Improvements

1. **Outbox Pattern:** Async message delivery with retry/backoff
2. **Auto-discovery of States:** Using ApplicationContext scanning
3. **Interface Segregation:** Smaller interfaces in ConversationContext
4. **Distributed Tracing:** OpenTelemetry integration
5. **Security:** Spring Security + OAuth2
6. **Rate Limiting:** Token bucket or sliding window
7. **Comprehensive Tests:** Unit + Integration with Testcontainers

## 👨‍💻 Contributing

Follow the established architecture and SOLID principles. See ARCHITECTURE_REVIEW.md for guidelines.

## 📄 License

MIT

---

**Last Updated:** 2026-08-04  
**Version:** 0.0.1-SNAPSHOT
