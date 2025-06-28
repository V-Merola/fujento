package com.fujentopj.fujento.module.users.domain.event;

import com.fujentopj.fujento.module.users.domain.event.marker.ModifiedByAware;
import com.fujentopj.fujento.module.users.domain.event.marker.ReasonAware;
import com.fujentopj.fujento.module.users.domain.model.snapshot.UserSnapshot;
import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Evento di dominio emesso quando un utente viene registrato.
 */
public record UserRegistered(
        String EVENT_TYPE,
        UserSnapshot snapshot,
        Instant occurredAt,
        UserId createdBy,
        Optional<String> reason

        ) implements DomainEvent, ModifiedByAware, ReasonAware {

    static String eventType = "UserRegistered";

    public static UserRegistered of(UserSnapshot snapshot ,UserId modifiedBy, String reason) {
        Objects.requireNonNull(snapshot.id(), "userId non può essere null");
        Objects.requireNonNull(modifiedBy, "modifiedBy non può essere null");


        return new UserRegistered(
                eventType,
                snapshot,
                Instant.now(),
                modifiedBy,
                Optional.ofNullable(reason)
        );
    }
    public static UserRegistered of(UserSnapshot snapshot, UserId createdBy) {
        return new UserRegistered(
                eventType,
                snapshot,
                Instant.now(),
                createdBy,
                Optional.of("Utente registrato con successo") // ✅ qui il messaggio di default
        );
    }
    @Override
    public UserId aggregateId() {
        return snapshot.id();
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    @Override
    public UserId modifiedBy() {
        return createdBy;
    }
}
