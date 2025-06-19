package com.fujentopj.fujento.module.users.application.event.publisher;

import com.fujentopj.fujento.module.users.domain.event.DomainEvent;
import com.fujentopj.fujento.module.users.port.out.DomainEventPublisher;

import java.util.List;

/**
 * Decorator: che arricchisce ogni evento con metadati prima della pubblicazione.
 * Usa DomainEventEnricher per aggiungere "modifiedBy" o altri campi.
 * Questo permette di centralizzare la logica di enrichment
 * e ridurre il codice boilerplate nei servizi di dominio.
 * Esempio: se un evento ha un campo "modifiedBy",
 * questo publisher lo arricchisce automaticamente
 * con l'ID dell'attore corrente
 * senza doverlo specificare manualmente in ogni chiamata di publish.
 * Questo è utile per tracciare chi ha modificato cosa
 * e per mantenere la coerenza nei metadati degli eventi.
 * * Vantaggi:
 * 1. Centralizza la logica di enrichment degli eventi.
 * 2. Riduce il boilerplate nei servizi di dominio.
 * 3. Facilita la tracciabilità delle modifiche.
 * 4. Consente di aggiungere metadati senza modificare gli eventi esistenti.
 * 5. Supporta facilmente nuovi campi di enrichment in futuro.
 * Usare nell’application service per pubblicare eventi arricchiti automaticamente.
 */
public class EnrichingDomainEventPublisher implements DomainEventPublisher {
    private final DomainEventPublisher delegate;
    private final String actorId;

    public EnrichingDomainEventPublisher(DomainEventPublisher delegate, String actorId) {
        this.delegate = delegate;
        this.actorId = actorId;
    }

    @Override
    public void publish(DomainEvent event) {
        DomainEvent enriched = DomainEventEnricher.enrichWithActor(event, actorId);
        delegate.publish(enriched);
    }

    @Override
    public void publishAll(List<DomainEvent> events) {
        events.forEach(this::publish);
    }
}
