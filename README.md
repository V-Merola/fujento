# Fujento

**Spring Modulith Application**  
Architettura guidata dal **Domain-Driven Design (DDD)** e strutturata secondo i principi dell'**Architettura Esagonale** (Hexagonal Architecture).

---

## 📦 Moduli Attualmente in Sviluppo

### 🔹 `user-core`
Modulo dedicato alla **registrazione** e alla **gestione anagrafica degli utenti**.

- **Approccio DDD**: il dominio guida la progettazione.  
- **Architettura Esagonale**: separazione chiara tra dominio, application layer e adapters.
- **Aggregate Root**: `User.java`
- **Layer Adapter (Infrastructure)**: implementato con **Spring** (Spring Data JPA, eventi, ecc.).
- Comunicazione tra moduli basata su **eventi Spring** (JSON-ready) per supportare l'evoluzione verso microservizi.

```
fujento/
├── user-core-module/
│   ├── domain/
│   ├── application/
│   ├── infrastructure/adapter/
│   ├── port/
│   └── mapper/
├── module_2/
├── module_3/
└── ...
```

## 🧠 Tecnologie & Principi

- Java 21+
- Spring Boot + Spring Modulith
- DDD, Hexagonal Architecture
- Event-driven design
- Test stratificati (unitari, application, integration)
- Factory methods, value objects immutabili, domain exceptions
- Separation of concerns tra i layer
- Configurazione modulare e visibilità tra moduli controllata

---

## ⚡ Event-Driven Architecture

Fujento utilizza un'architettura **event-driven** basata su:

- **Eventi di Dominio (Domain Events)**: generati all’interno degli Aggregati per rappresentare fatti significativi (es. `UserRegisteredEvent`).
- **Spring Application Events**: propagano gli eventi all'interno del contesto modulare in modo asincrono e disaccoppiato.
- **Eventi JSON-ready**: per preparare i moduli alla serializzazione e alla trasmissione esterna (Kafka-ready).
- **Modular Event Publication**: tramite `@ApplicationEventPublisher` e `@EventListener` tra moduli annotati con `@ModuleExport` / `@ModuleImport`.

📌 L’obiettivo è **separare i moduli in modo chiaro** e **facilitare la futura transizione verso microservizi**, dove questi eventi potranno essere pubblicati su **Kafka** o altri message broker.

Esempio:

```java
// Dominio
public record UserRegisteredEvent(UserId id, Email email) {}

// Use Case
eventPublisher.publishEvent(new UserRegisteredEvent(user.getId(), user.getEmail()));

// Altro modulo
@EventListener
void handle(UserRegisteredEvent event) {
    // Reazione cross-modulo
}
```

---
## Esempio di flusso di un caso d'uso nel modulo `user-core`: REGISTER USER

```
[1] Richiesta HTTP
    |
    v
┌──────────────────────────────────────────────┐
│ RegisterUserRequestDTO                       │
│ (DTO pubblico nel layer REST/controller)     │
└──────────────────────────────────────────────┘
    |
    v
┌──────────────────────────────────────────────┐
│ UserController                                │
│ (Adapter IN)                                  │
│ - Converte DTO → RegisterUserCommand         │
│ - Chiama porta IN: RegisterUserUseCase       │
└──────────────────────────────────────────────┘
    |
    v
┌──────────────────────────────────────────────┐
│ RegisterUserUseCase                           │
│ (Porta IN - interfaccia nel layer Application)│
└──────────────────────────────────────────────┘
    |
    v
┌──────────────────────────────────────────────┐
│ RegisterUserService                           │
│ (Application Service)                         │
│ - Valida email/nickname                       │
│ - Usa PasswordHasherPort.hash()               │
│ - Crea l’Aggregate: User.register(...)        │
│ - Chiama: userRepository.save(user)           │
└──────────────────────────────────────────────┘
    |
    v
┌──────────────────────────────────────────────┐
│ UserRepository                                │
│ (Porta OUT - interfaccia)                     │
└──────────────────────────────────────────────┘
    |
    v
┌──────────────────────────────────────────────┐
│ JpaUserRepository                             │
│ (Adapter OUT - implementa UserRepository)     │
│ - Usa UserPersistenceMapper                   │
│ - Converte User → UserJpaEntity               │
│ - Chiama: springDataUserRepository.save(...)  │
└──────────────────────────────────────────────┘
    |
    v
┌──────────────────────────────────────────────┐
│ SpringDataUserRepository                      │
│ (Interfaccia estende JpaRepository)           │
└──────────────────────────────────────────────┘
    |
    v
┌──────────────────────────────────────────────┐
│ JpaRepository<UserJpaEntity, UUID>            │
│ (Spring Data, implementazione runtime)        │
│ - Salva nel DB                                │
└──────────────────────────────────────────────┘
```




