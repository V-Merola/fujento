package com.fujentopj.fujento.module.users.domain.event;

import com.fujentopj.fujento.module.users.domain.event.marker.ModifiedByAware;
import com.fujentopj.fujento.module.users.domain.event.marker.ReasonAware;
import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public record UserActivated(
        UserId userId,
        Instant occurredAt,
        UserId modifiedBy,
        Optional<String> reason,
        Optional<String> correlationId,
        Optional<String> traceId
 ) implements DomainEvent, ModifiedByAware, ReasonAware {


public static UserActivated of(UserId userId, UserId modifiedBy, String reason) {
    Objects.requireNonNull(userId, "userId non può essere null");
    Objects.requireNonNull(modifiedBy, "modifiedBy non può essere null");

    return new UserActivated(
            userId,
            Instant.now(),
            modifiedBy,
            Optional.ofNullable(reason),
            Optional.empty(),
            Optional.empty()
    );
}

    public static UserActivated withoutReason(UserId userId, UserId modifiedBy) {
        return new UserActivated(
                userId,
                Instant.now(),
                modifiedBy,
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );
    }
    @Override
    public UserId aggregateId() {
        return userId;
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
