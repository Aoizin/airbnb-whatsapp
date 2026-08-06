# 🏛️ ARQUITETURA VISUAL - DIAGRAMA E FLUXOS

Representação visual da arquitetura após refatoração.

---

## 1. LAYERED ARCHITECTURE

```
┌─────────────────────────────────────────────────────────────────┐
│                      CLIENT LAYER                               │
│  ┌───────────────┐  ┌─────────────┐  ┌──────────────────────┐  │
│  │ Evolution API │  │  Browser    │  │ Monitoring/Metrics   │  │
│  │ (WhatsApp)    │  │ (Admin UI)  │  │ (Prometheus)         │  │
│  └───────┬───────┘  └──────┬──────┘  └──────────┬───────────┘  │
└──────────┼────────────────┼────────────────────┼───────────────┘
           │                │                    │
    HTTP   │                │                    │ HTTP
  Webhook  │                │                    │
           ▼                ▼                    ▼
┌─────────────────────────────────────────────────────────────────┐
│                    ADAPTER LAYER (Inbound)                      │
│  ┌──────────────────┐  ┌──────────────┐  ┌──────────────────┐  │
│  │ WebhookController│  │AdminController│  │HealthController │  │
│  │ (POST webhook)   │  │ (MVC views)   │  │ (Monitoring)     │  │
│  └────────┬─────────┘  └───────┬──────┘  └──────────┬───────┘  │
│           │                    │                    │           │
│           │ @Valid             │                    │           │
│           │ WebhookRequest     │                    │           │
│           └────┬───────────────┴────────────────────┘           │
└────────────────┼────────────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────────────┐
│                  APPLICATION LAYER (Services)                   │
│  ┌──────────────────────┐    ┌──────────────────────┐           │
│  │ConversationService   │    │AdminDashboardService │           │
│  │ - processInbound()   │    │ - listHospedagens()  │           │
│  │ - createSession()    │    │ - getById()          │           │
│  └──────────┬───────────┘    └──────────┬───────────┘           │
│             │                           │                       │
│  ┌──────────┴──────────────────────────┴─────┐                 │
│  │     OutboundMessagingService (NEW)        │                 │
│  │  - sendReply()                            │                 │
│  │  - sendImage()                            │                 │
│  │  - sendDocument()                         │                 │
│  │  - sendLocation()                         │                 │
│  └──────────┬─────────────────────────────────┘                 │
│             │ Depends on Abstraction                            │
│  ┌──────────▼────────────────────────────────┐                 │
│  │        MessagingPort (Interface)          │                 │
│  │ (Dependency Inversion - DIP)              │                 │
│  └──────────┬─────────────────────────────────┘                 │
└────────────┼──────────────────────────────────────────────────┘
             │
             ▼
┌─────────────────────────────────────────────────────────────────┐
│                  ADAPTER LAYER (Outbound)                       │
│  ┌──────────────────────┐         ┌──────────────────────────┐  │
│  │  EvolutionClient     │         │  Repositories (JPA)      │  │
│  │ implements           │         │                          │  │
│  │ MessagingPort        │         │ HospedagemRepository     │  │
│  │ (Evolution API)      │         │ HospedeRepository        │  │
│  │                      │         │ SessaoWhatsappRepository │  │
│  └──────────┬───────────┘         └────────┬─────────────────┘  │
│             │                              │                    │
└─────────────┼──────────────────────────────┼────────────────────┘
              │                              │
        HTTP  │ REST                    JDBC │
              ▼                              ▼
    ┌─────────────────────┐      ┌──────────────────┐
    │ Evolution API       │      │   PostgreSQL     │
    │ (External Service)  │      │   Database       │
    └─────────────────────┘      └──────────────────┘
```

---

## 2. STATE MACHINE FLOW

```
Webhook Recebido (+5511999999999, "oi")
    │
    ▼
┌──────────────────────────────────────────┐
│ ConversationService.processInbound()     │
│ - Recupera ou cria SessaoWhatsapp        │
│ - Atualiza lastActivityAt                │
│ - Cria ConversationContext               │
└──────────┬───────────────────────────────┘
           │
           ▼
┌──────────────────────────────────────────┐
│ MessageProcessor.process()               │
│ - Obtém state atual (ex: INICIO)        │
│ - StateFactory retorna handler           │
│ - Delega ao handler                      │
└──────────┬───────────────────────────────┘
           │
    ┌──────┴────────────────────────────────────────┐
    │                                               │
    ▼                                               ▼
┌────────────────┐                         ┌─────────────────┐
│ InicioState    │                         │ ApartamentoState│
│ - Válida nada  │ StateResult             │ - Valida texto  │
│ - Responde:    │ ────────────►           │ - Salva apt.    │
│   "Olá!"       │ {nextState:             │ - Responde:     │
│ - Próx:        │  APARTAMENTO,           │   "Qual apart?" │
│   APARTAMENTO  │  replyMessage: "..."}   │ - Próx:         │
└────────────────┘                         │   DATA_ENTRADA  │
                                           └─────────────────┘

    ┌─────────────────────────────────────────────────┐
    │ (Continua a cada mensagem...)                   │
    │                                                 │
    │ DATA_ENTRADA → DATA_SAIDA → RESPONSAVEL →      │
    │ QTD_HOSPEDES → NOME_HOSPEDE → PLACA →          │
    │ OBSERVACAO → FINALIZADO                        │
    └─────────────────────────────────────────────────┘

           ▼
┌──────────────────────────────────────────┐
│ ConversationService                      │
│ - Persiste novo state em SessaoWhatsapp │
│ - Retorna replyMessage                   │
└──────────┬───────────────────────────────┘
           │
           ▼
┌──────────────────────────────────────────┐
│ WebhookController                        │
│ - Obtém reply                            │
│ - Chama OutboundMessagingService         │
│ - Retorna 200 ao Evolution API           │
└──────────┬───────────────────────────────┘
           │
           ▼
┌──────────────────────────────────────────┐
│ OutboundMessagingService.sendReply()    │
│ - Delegar a MessagingPort (interface)   │
└──────────┬───────────────────────────────┘
           │
           ▼
┌──────────────────────────────────────────┐
│ EvolutionClient.sendText()               │
│ - Valida input                           │
│ - Chama sendMessage() genérico           │
│ - POST para Evolution API                │
│ - Retorna boolean                        │
└──────────┬───────────────────────────────┘
           │
           ▼ HTTP
    ┌──────────────────┐
    │ Evolution API    │
    │ → WhatsApp       │
    │ → Usuário        │
    └──────────────────┘
```

---

## 3. DEPENDENCY GRAPH

```
ANTES (Acoplado):
┌─────────────────┐
│ WebhookController
│ depends on:
├─ ConversationService
└─ EvolutionClient ◄─── CONCRETO! Difícil testar
└──────────┬──────┘


DEPOIS (Desacoplado - DIP):
┌────────────────────────────────────────────────────────────┐
│ WebhookController                                          │
│ depends on:                                                │
├─ ConversationService (service)                            │
├─ OutboundMessagingService (service)                       │
│  │                                                         │
│  └─ depends on:                                           │
│     └─ MessagingPort (INTERFACE) ◄─── ABSTRAÇÃO! Testável │
│        │                                                  │
│        └─ implemented by:                                 │
│           └─ EvolutionClient (adapter)                   │
└────────────────────────────────────────────────────────────┘

BENEFÍCIO: Trocar EvolutionClient por MockMessagingPort
em testes é trivial!
```

---

## 4. ANTES vs DEPOIS - Código

```
╔════════════════════════════════════════════════════════════════╗
║ ANTES: EvolutionClient - Duplicação (70 linhas)               ║
╚════════════════════════════════════════════════════════════════╝

public boolean sendText(String to, String body) {
  TextMessageRequest req = TextMessageRequest.builder()...
  String url = props.getEndpoints().getSendText();
  try {
    var resp = evolutionWebClient.post().uri(url)
      .bodyValue(req).retrieve()...
    log.info("sendText to={} success", to);
    return true;
  } catch (WebClientResponseException wex) {
    log.error("Evolution API error...", to, wex.getStatusCode()...);
  }
  return false;
}

public boolean sendImage(String to, String imageUrl, String caption) {
  ImageMessageRequest req = ImageMessageRequest.builder()...
  String url = props.getEndpoints().getSendImage();
  try {
    var resp = evolutionWebClient.post().uri(url)    ◄─── DUPLICADO!
      .bodyValue(req).retrieve()...                  ◄─── DUPLICADO!
    log.info("sendImage to={} success", to);        ◄─── DUPLICADO!
    return true;                                    ◄─── DUPLICADO!
  } catch (WebClientResponseException wex) {        ◄─── DUPLICADO!
    log.error("Evolution API error...");
  }                                                 ◄─── DUPLICADO!
  return false;
}

// ... sendDocument() - DUPLICADO NOVAMENTE!
// ... sendLocation() - DUPLICADO NOVAMENTE!


╔════════════════════════════════════════════════════════════════╗
║ DEPOIS: EvolutionClient - Refatorado (15 linhas principais)   ║
╚════════════════════════════════════════════════════════════════╝

// Método genérico - Template Method Pattern
private boolean sendMessage(String messageType, String to, Object payload) {
  try {
    var resp = evolutionWebClient.post()
      .uri(getEndpoint(messageType))  ◄─── Delegado
      .bodyValue(payload)
      .retrieve()...
    log.info("Evolution API success messageType={} to={}", messageType, to);
    return true;
  } catch (Exception ex) {
    log.error("Error sending {} to {}: {}", messageType, to, ex.getMessage());
  }
  return false;
}

// Endpoint strategy
private String getEndpoint(String messageType) {
  return switch (messageType) {
    case "text" -> props.getEndpoints().getSendText();
    case "image" -> props.getEndpoints().getSendImage();
    case "document" -> props.getEndpoints().getSendDocument();
    case "location" -> props.getEndpoints().getSendLocation();
    default -> throw new IllegalArgumentException(...);
  };
}

// Public methods - apenas delegam
public boolean sendText(String to, String body) {
  TextMessageRequest req = TextMessageRequest.builder().to(to)
    .text(new TextMessageRequest.Content(body)).build();
  return sendMessage("text", to, req);  ◄─── Delega
}

public boolean sendImage(String to, String imageUrl, String caption) {
  ImageMessageRequest req = ImageMessageRequest.builder().to(to)
    .image(ImageMessageRequest.Content.builder()
      .url(imageUrl).caption(caption).build()).build();
  return sendMessage("image", to, req);  ◄─── Delega
}

✅ RESULTADO: 79% redução de duplicação
             Mantém funcionalidade 100%
             Mais fácil de estender (novo tipo = 1 case + 1 método)
```

---

## 5. BENEFÍCIOS VISUAIS

```
TESTABILIDADE

╔════════════════════════════════════╗
║ ANTES: Difícil mockar             ║
╚════════════════════════════════════╝

  WebhookController(ConversationService, EvolutionClient)
                                         │
                                    precisa de:
                                    WebClient.post()
                                    + EvolutionProperties
                                    + real API calls

  @Test
  void webhook() {
    // Como moco EvolutionClient? Preciso mockar WebClient também
    // Complexo, frágil
  }


╔════════════════════════════════════╗
║ DEPOIS: Fácil mockar              ║
╚════════════════════════════════════╝

  WebhookController(ConversationService, OutboundMessagingService)
                                    │
                            depends on MessagingPort
                                    │
  @Mock MessagingPort messagingPort; // 1 interface simples

  @Test
  void webhook() {
    when(messagingPort.sendText(anyString(), anyString()))
      .thenReturn(true);
    // Pronto! Simples, direto, confiável
  }
```

---

## 6. ESTRUTURA DE PASTAS VISUAL

```
airbnb-whatsapp/
│
├── src/main/java/com/acme/airbnbwhatsapp/
│   │
│   ├── AirbnbWhatsappApplication.java     ✅ NEW: Startup
│   │
│   ├── adapters/                          INBOUND/OUTBOUND
│   │   ├── in/web/
│   │   │   ├── WebhookController.java     ✅ IMPROVED
│   │   │   ├── AdminController.java
│   │   │   ├── HealthController.java      ✅ NEW
│   │   │   └── WebhookRequest.java        ✅ IMPROVED (validation)
│   │   │
│   │   └── out/
│   │       ├── evolution/
│   │       │   ├── MessagingPort.java     ✅ NEW (Interface)
│   │       │   ├── EvolutionClient.java   ✅ IMPROVED (DRY)
│   │       │   ├── EvolutionProperties.java
│   │       │   └── dto/
│   │       │       ├── TextMessageRequest.java
│   │       │       ├── ImageMessageRequest.java
│   │       │       ├── DocumentMessageRequest.java
│   │       │       └── LocationMessageRequest.java
│   │       │
│   │       └── persistence/repository/
│   │           ├── HospedagemRepository.java
│   │           ├── HospedagemRepositoryCustom.java
│   │           ├── HospedagemRepositoryImpl.java
│   │           ├── HospedeRepository.java
│   │           └── SessaoWhatsappRepository.java
│   │
│   ├── application/                       USE CASES LAYER
│   │   └── dto/
│   │       ├── HospedeDTO.java
│   │       ├── HospedagemDTO.java
│   │       ├── HospedagemSummaryDTO.java
│   │       └── SessaoWhatsappDTO.java
│   │
│   ├── config/                            CONFIGURATION
│   │   ├── EvolutionConfiguration.java
│   │   └── GlobalExceptionHandler.java   ✅ NEW
│   │
│   ├── domain/                            DOMAIN LAYER
│   │   └── model/
│   │       ├── Hospede.java
│   │       ├── Hospedagem.java
│   │       ├── SessaoWhatsapp.java
│   │       └── enums/
│   │           ├── ConversationState.java
│   │           ├── HospedagemStatus.java
│   │           └── HospedagemOrigem.java
│   │
│   └── service/                           APPLICATION SERVICES
│       ├── ConversationService.java        ✅ IMPROVED (START→INICIO)
│       ├── OutboundMessagingService.java   ✅ NEW (SRP)
│       ├── AdminDashboardService.java
│       ├── ConversationContext.java
│       ├── MessageProcessor.java
│       ├── StateFactory.java               ✅ IMPROVED (START→INICIO)
│       ├── StateResult.java
│       └── state/
│           ├── StateHandler.java (Interface)
│           ├── InicioState.java
│           ├── ApartamentoState.java
│           ├── DataEntradaState.java
│           ├── DataSaidaState.java
│           ├── ResponsavelState.java
│           ├── QtdHospedesState.java
│           ├── NomeHospedeState.java
│           ├── PlacaState.java
│           ├── ObservacaoState.java
│           └── FinalizadoState.java
│
├── src/main/resources/
│   ├── application.yml                    ✅ IMPROVED
│   ├── application-prod.yml               ✅ NEW (Profile)
│   ├── templates/admin/
│   │   ├── layout.html
│   │   ├── dashboard.html
│   │   └── hospedagens/
│   │       ├── list.html
│   │       └── detail.html
│   │
│   └── db/migration/
│       ├── V1__init_tables.sql
│       └── V2__hospedagem_add_fields.sql
│
├── pom.xml
├── README.md                              ✅ IMPROVED
├── ARCHITECTURE_REVIEW.md                 ✅ NEW
├── REVISION_SUMMARY.md                    ✅ NEW
├── FUTURE_ROADMAP.md                      ✅ NEW
└── VALIDATION_CHECKLIST.md                ✅ NEW
```

---

## 7. MATRIZ DE RESPONSABILIDADES

```
┌──────────────────────────┬──────────┬──────────┬──────────┐
│ Classe/Componente        │ Entrada  │ Negócio  │ Saída    │
├──────────────────────────┼──────────┼──────────┼──────────┤
│ WebhookController        │ ✅       │          │          │
│ AdminController          │ ✅       │          │ View     │
│ HealthController         │ ✅       │          │          │
├──────────────────────────┼──────────┼──────────┼──────────┤
│ ConversationService      │          │ ✅       │          │
│ OutboundMessagingService │          │ ✅       │ EvAP     │
│ AdminDashboardService    │          │ ✅       │          │
├──────────────────────────┼──────────┼──────────┼──────────┤
│ StateHandler (interface) │          │ ✅       │          │
│ InicioState              │          │ ✅       │          │
│ ... (10 states)          │          │ ✅       │          │
├──────────────────────────┼──────────┼──────────┼──────────┤
│ MessagingPort (interface)│          │          │ ✅       │
│ EvolutionClient          │          │          │ ✅ API   │
│ Repository interfaces    │          │          │ ✅ DB    │
└──────────────────────────┴──────────┴──────────┴──────────┘
```

---

## 8. FLUXO DE TESTES

```
┌─────────────────────────────────────────────────────────────┐
│ UNIT TESTS (State Handlers)                                 │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│ @Test testApartamentoState_validInput() {                  │
│   InicioState state = new InicioState();                    │
│   StateResult result = state.handle(context, "501");        │
│   assert result.nextState == APARTAMENTO;                  │
│ }                                                           │
│                                                             │
│ @Test testDataEntradaState_invalidFormat() {               │
│   DataEntradaState state = new DataEntradaState();         │
│   StateResult result = state.handle(context, "invalid");    │
│   assert result.nextState == DATA_ENTRADA; // retry       │
│ }                                                           │
│                                                             │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ INTEGRATION TESTS (Service + Repository)                   │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│ @DataJpaTest                                               │
│ @Test testConversationFlow_webhook2confirmation() {        │
│   // 1. Enviar webhook                                     │
│   // 2. Validar estado persistido                          │
│   // 3. Validar dados salvos                               │
│ }                                                           │
│                                                             │
│ @SpringBootTest                                            │
│ @Test testWebhookController_callsMessagingService() {      │
│   // 1. Mock MessagingPort                                 │
│   // 2. Chamar POST /webhook                               │
│   // 3. Validar que sendText foi chamado                   │
│ }                                                           │
│                                                             │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ E2E TESTS (End-to-End)                                      │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│ @SpringBootTest                                            │
│ @Testcontainers                                            │
│ @Test testCompleteRegistrationFlow() {                     │
│   // 1. Start app + postgres container                     │
│   // 2. Send webhook messages (INICIO → FINALIZADO)       │
│   // 3. Verify hospede created in DB                       │
│   // 4. Verify MessagingPort.sendText called 10x          │
│ }                                                           │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 9. DEPLOYMENT ARCHITECTURE

```
┌──────────────────────────────────────────────────────────┐
│ LOAD BALANCER (nginx)                                    │
└────────────┬─────────────────────────────────────────────┘
             │
             ├─ /health ──────► Health Check ─────────────────┐
             │                                               │
             ├─ /api/webhook/* ─ Round Robin                │
             │                   ├─ App Pod 1 (8080)        │
             │                   ├─ App Pod 2 (8080)        │
             │                   └─ App Pod N (8080)        │
             │                                               │
             ├─ /admin/* ──────► Session Affinity           │
             │                                               │
             └─ /swagger-ui ──► Documentation               │
                                                            │
     ┌───────────────────────────────────────────────────┐
     │ DATABASE                                          │
     ├───────────────────────────────────────────────────┤
     │ PostgreSQL 12+ (Primary + Replica)               │
     │                                                  │
     │ Tables:                                          │
     │ - hospede                                        │
     │ - hospedagem                                     │
     │ - sessao_whatsapp                                │
     │ - outbox_message (Future)                        │
     └───────────────────────────────────────────────────┘
```

---

*Diagrama atualizado: 2026-08-04*

