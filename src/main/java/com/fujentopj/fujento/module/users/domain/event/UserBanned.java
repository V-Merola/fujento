package com.fujentopj.fujento.module.users.domain.event;

import com.fujentopj.fujento.module.users.domain.event.marker.ModifiedByAware;
import com.fujentopj.fujento.module.users.domain.event.marker.ReasonAware;
import com.fujentopj.fujento.module.users.domain.model.snapshot.UserSnapshot;
import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;

import java.time.Instant;
import java.util.Optional;

public record UserBanned(
        UserSnapshot snapshot,
        Instant occurredAt,
        UserId modifiedBy,
        Optional<String> reason,
        Optional<String> correlationId,
        Optional<String> traceId
) implements DomainEvent, ModifiedByAware, ReasonAware {
    public static UserBanned of(UserSnapshot snapshot, UserId modifiedBy, String reason) {
        return new UserBanned(
                snapshot,
                Instant.now(),
                modifiedBy,
                Optional.ofNullable(reason),
                Optional.empty(),
                Optional.empty()
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
    public int version() {
        return 1; // Versione dell'evento
    }
}
