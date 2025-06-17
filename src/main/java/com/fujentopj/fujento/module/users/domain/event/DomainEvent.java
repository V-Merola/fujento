package com.fujentopj.fujento.module.users.domain.event;

import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;

import java.time.Instant;

/**
 * Marker interface per tutti gli eventi di dominio interni.
 * Può essere utilizzata per raccogliere e pubblicare eventi in modo generico.
 */

/*
* Se in futuro questi eventi dovranno essere esportati fuori dal
* sistema (es. Kafka, WebSocket), potresti aggiungere occurredOn,
* correlationId o metadati aggiuntivi, ma attualmente non è necessario.
*/
public interface DomainEvent {
    UserId agggregateId();

    Instant occuredAt();
}
