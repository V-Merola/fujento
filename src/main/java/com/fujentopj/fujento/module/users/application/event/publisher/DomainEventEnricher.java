package com.fujentopj.fujento.module.users.application.event.publisher;

import com.fujentopj.fujento.module.users.domain.event.DomainEvent;

import java.lang.reflect.Method;

/**
 * Utility per arricchire eventi con metadata (es. modifiedBy).
 * Funziona per eventi che hanno campi "modifiedBy" opzionali.
 */
public class DomainEventEnricher {

    public static <T extends DomainEvent> T enrichWithActor(T event, String actor) {
        try {
            Method withActorMethod = event.getClass().getMethod("withModifiedBy", String.class);
            return (T) withActorMethod.invoke(event, actor);
        } catch (Exception e) {
            return event; // fallback, l'evento non supporta enrichment
        }
    }
}
