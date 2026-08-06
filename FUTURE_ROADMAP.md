# 🎯 PLANO DE MELHORIAS FUTURAS

## Roadmap Técnico - Priorizado

Documento com tarefas específicas para continuar melhorando a arquitetura.

---

## FASE 1: OUTBOX PATTERN (P0 - 1-2 sprints)

Implementar entrega confiável de mensagens com retry automático.

### Problema Resolvido
- ❌ WebhookController aguarda Evolution API (pode timeout/falhar)
- ✅ Após: MessageBuffer persiste, Worker envia com retry

### Tarefas
- [ ] Criar entidade `OutboxMessage` (JPA)
  ```java
  @Entity
  class OutboxMessage {
    UUID id;
    String to;
    String message;
    MessageType type;
    OutboxStatus status; // PENDING, SENT, FAILED
    int retryCount;
    Instant createdAt;
    Instant sentAt;
  }
  ```
- [ ] Criar Flyway migration V3 com tabela outbox
- [ ] Refatorar OutboundMessagingService para persistir em outbox
- [ ] Criar `OutboxWorker` com @Scheduled
  ```java
  @Component
  public class OutboxWorker {
    @Scheduled(fixedDelay = 5000)
    public void processOutbox() { /* retry failed messages */ }
  }
  ```
- [ ] Adicionar retry policy (exponential backoff)
- [ ] Adicionar testes

### Benefício
- Webhook retorna 200 imediatamente ✅
- Garantia de entrega eventual
- Observabilidade (status por mensagem)

---

## FASE 2: AUTO-DISCOVERY DE STATES (P1 - 1 sprint)

Eliminar injeção manual de 10 states no StateFactory.

### Problema Resolvido
- ❌ StateFactory constructor requer todos os 10 states como parâmetros
- ✅ Após: Auto-discovery via annotation scanning

### Tarefas
- [ ] Criar anotação customizada
  ```java
  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface ConversationStateHandler {
    ConversationState value();
  }
  ```
- [ ] Anotar todos os state handlers
  ```java
  @Component
  @ConversationStateHandler(ConversationState.INICIO)
  public class InicioState implements StateHandler { }
  ```
- [ ] Refatorar StateFactory
  ```java
  @Component
  public class StateFactory {
    public StateFactory(ApplicationContext context) {
      // Auto-scan beans com @ConversationStateHandler
      context.getBeansWithAnnotation(ConversationStateHandler.class)
        .forEach((name, bean) -> {
          ConversationStateHandler annot = 
            bean.getClass().getAnnotation(ConversationStateHandler.class);
          map.put(annot.value(), (StateHandler) bean);
        });
    }
  }
  ```
- [ ] Remover injeção manual de states
- [ ] Testes unitários para StateFactory

### Benefício
- Escalável (novo state = apenas @Component + @ConversationStateHandler)
- Menos frágil (sem listar manualmente)
- Mais elegante (segue Spring conventions)

---

## FASE 3: INTERFACE SEGREGATION (P1 - 1 sprint)

Segregar ConversationContext em interfaces menores.

### Problema Resolvido
- ❌ ConversationContext expõe todos os repositórios
- ✅ Após: Interfaces específicas por domínio

### Tarefas
- [ ] Criar interfaces segregadas
  ```java
  public interface GuestDataProvider {
    Optional<Hospede> getHospede();
    Hospede saveHospede(Hospede h);
  }
  
  public interface BookingDataProvider {
    Optional<Hospedagem> getHospedagem();
    Hospedagem saveHospedagem(Hospedagem h);
  }
  ```
- [ ] ConversationContext implementa interfaces
- [ ] Atualizar StateHandlers para depender de interfaces, não repository
- [ ] Testes

### Benefício
- Menor interface (cada state só vê o que precisa)
- Mais testável (mocks menores)
- Melhor design (Interface Segregation Principle)

---

## FASE 4: TESTES AUTOMATIZADOS (P0 - 2 sprints)

Cobertura de testes para confiabilidade.

### Testes Unitários
- [ ] StateHandler tests (cada um)
  ```java
  @Test
  void apartamentoState_validInput_savesAndTransitions() { }
  
  @Test
  void apartamentoState_emptyInput_staysInSameState() { }
  ```
- [ ] EvolutionClient tests (com WireMock)
- [ ] AdminDashboardService tests (com @DataJpaTest)
- [ ] OutboundMessagingService tests

### Testes de Integração
- [ ] ConversationService integration test (Testcontainers)
- [ ] WebhookController integration test
- [ ] AdminController integration test
- [ ] Complete flow test (webhook → states → db)

### Coverage
- Target: 80%+ coverage
- Use: JaCoCo plugin

Adicionar ao pom.xml:
```xml
<plugin>
  <groupId>org.jacoco</groupId>
  <artifactId>jacoco-maven-plugin</artifactId>
  <version>0.8.8</version>
  <executions>
    <execution>
      <goals>
        <goal>prepare-agent</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```

---

## FASE 5: SEGURANÇA (P1 - 2 sprints)

Proteger endpoints e webhook.

### Tarefas
- [ ] Adicionar Spring Security
  ```xml
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
  </dependency>
  ```
- [ ] Implementar WebSecurityConfig
  ```java
  @Configuration
  public class SecurityConfig {
    // /admin/** → autenticado
    // /api/webhook/** → validar HMAC signature
    // /health → público
  }
  ```
- [ ] Adicionar HMAC signature validation para webhook
  ```java
  public class HmacValidator {
    public static boolean isValid(String payload, String signature, String secret) {
      String computed = HmacUtils.hmacSha256Hex(secret, payload);
      return MessageDigest.isEqual(
        computed.getBytes(),
        signature.getBytes()
      );
    }
  }
  ```
- [ ] Rate limiting
- [ ] CORS configuration
- [ ] HTTPS/TLS (em produção)

---

## FASE 6: OBSERVABILIDADE AVANÇADA (P2 - 2 sprints)

Distributed tracing e métricas customizadas.

### Tarefas
- [ ] Adicionar OpenTelemetry
  ```xml
  <dependency>
    <groupId>io.opentelemetry.javaagent</groupId>
    <artifactId>opentelemetry-javaagent</artifactId>
  </dependency>
  ```
- [ ] Integrar com Jaeger (distributed tracing)
- [ ] Adicionar custom metrics via Micrometer
  ```java
  private MeterRegistry meterRegistry;
  
  meterRegistry.counter("webhook.received").increment();
  meterRegistry.timer("conversation.state.duration").record(...);
  ```
- [ ] Prometheus scraping config
- [ ] Dashboard Grafana (opcional)

---

## FASE 7: PERFORMANCE & CACHING (P2 - 1 sprint)

Otimizar queries e adicionar cache.

### Tarefas
- [ ] Análise de queries lentas
  - [ ] Ativar Hibernate query logging em dev
  - [ ] Usar JPA.getMetadata() para N+1 detection
- [ ] Adicionar índices no PostgreSQL (se necessário)
  ```sql
  CREATE INDEX idx_hospedagem_sessao_created ON hospedagem(sessao_whatsapp_id, created_at);
  ```
- [ ] Implementar Spring Cache
  ```xml
  <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
  </dependency>
  ```
  ```java
  @Cacheable("hospedagens")
  public HospedagemSummaryDTO getById(UUID id) { }
  ```
- [ ] Adicionar Redis (opcional)
  ```properties
  spring.redis.host=localhost
  spring.redis.port=6379
  ```

---

## FASE 8: ADMIN DASHBOARD MELHORIAS (P2 - 1 sprint)

UI/UX enhancements.

### Tarefas
- [ ] Adicionar sorting por coluna na tabela
- [ ] Adicionar export para CSV
- [ ] Adicionar ações inline (mudar status, editar)
- [ ] Melhorar detalhes (timeline de mensagens, hospede info)
- [ ] Adicionar breadcrumbs
- [ ] Dark mode (Bootstrap option)

---

## FASE 9: DOCUMENTAÇÃO API (P0 - 1 sprint)

Adicionar OpenAPI/Swagger documentation.

### Tarefas
- [ ] Adicionar anotações @OpenAPIDefinition, @Operation
  ```java
  @PostMapping
  @Operation(summary = "Receber webhook do Evolution API")
  public ResponseEntity<?> receive(@Valid @RequestBody WebhookRequest req) { }
  ```
- [ ] Gerar Swagger docs automático
- [ ] Publicar docs em produção
- [ ] Criar exemplos de payload

---

## FASE 10: INFRAESTRUTURA (P1 - 1 sprint)

Docker e deployment.

### Tarefas
- [ ] Criar Dockerfile (multi-stage)
  ```dockerfile
  FROM maven:3.8-openjdk-21 AS builder
  COPY . /build
  WORKDIR /build
  RUN mvn clean package
  
  FROM openjdk:21-slim
  COPY --from=builder /build/target/app.jar /app/app.jar
  ENTRYPOINT ["java", "-jar", "/app/app.jar"]
  ```
- [ ] Criar docker-compose.yml (app + postgres)
- [ ] Kubernetes manifests (deployment, service, configmap)
- [ ] Health checks em containers
- [ ] Secrets management

---

## MATRIZ DE PRIORIZAÇÃO

| Fase | P0 | Duração | Dependências |
|------|----|---------|----|
| 1. Outbox Pattern | ✅ P0 | 1-2 sp | Nenhuma |
| 2. Auto-discovery | ✅ P1 | 1 sp | Nenhuma |
| 3. Interface Segregation | ✅ P1 | 1 sp | Nenhuma |
| 4. Testes | ✅ P0 | 2 sp | Nenhuma |
| 5. Segurança | ✅ P1 | 2 sp | Nenhuma |
| 6. Observabilidade | ⚠️ P2 | 2 sp | 5 |
| 7. Performance | ⚠️ P2 | 1 sp | Nenhuma |
| 8. Admin UI | ⚠️ P2 | 1 sp | Nenhuma |
| 9. API Docs | ✅ P0 | 1 sp | Nenhuma |
| 10. Infra | ✅ P1 | 1 sp | Nenhuma |

---

## RECURSOS ÚTEIS

### Spring Boot
- https://spring.io/projects/spring-boot
- https://docs.spring.io/spring-boot/docs/current/reference/

### Clean Architecture
- Robert C. Martin - "Clean Architecture"
- https://blog.cleancoder.com/

### SOLID Principles
- https://en.wikipedia.org/wiki/SOLID
- https://www.baeldung.com/solid-principles

### Testing
- https://testcontainers.com/
- https://wiremock.org/
- https://site.mockito.org/

### Observability
- https://opentelemetry.io/
- https://prometheus.io/
- https://www.jaegertracing.io/

---

## TEMPLATE PARA CRIAR ISSUE

```markdown
## Fase X: [Nome]

### Descrição
[O que fazer]

### Checklist
- [ ] Task 1
- [ ] Task 2

### Benefício
[Por que fazer]

### Critérios de Aceição
- [X] deve funcionar
- [X] deve ter testes
- [X] deve ter logs

### Tempo Estimado
X horas / Y dias
```

---

*Documento atualizado: 2026-08-04*  
*Próxima revisão: Após completar Fase 1*

