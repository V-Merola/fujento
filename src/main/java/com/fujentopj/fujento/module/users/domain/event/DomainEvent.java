package com.fujentopj.fujento.module.users.domain.event;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;

import java.time.Instant;
import java.util.Optional;

/**
 * Marker interface per tutti gli eventi di dominio interni.
 * Può essere utilizzata per raccogliere e pubblicare eventi in modo generico.
 */

/*
* Se in futuro questi eventi dovranno essere esportati fuori dal
* sistema (es. Kafka, WebSocket), potresti aggiungere occurredOn,
* correlationId o metadati aggiuntivi, ma attualmente non è necessario.
*/
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,      // Usa il nome della classe
        include = JsonTypeInfo.As.PROPERTY,
        property = "TYPE_EVENT"                // ← campo aggiunto nel JSON
)
public interface DomainEvent {
    UserId aggregateId();

    Instant occurredAt();

    default Optional<String> correlationId(){
        return Optional.empty();
    }
    default Optional<String> traceId(){
        return Optional.empty();
    }

    default int version(){
        return 1; // Default version, can be overridden if needed
    }


}
