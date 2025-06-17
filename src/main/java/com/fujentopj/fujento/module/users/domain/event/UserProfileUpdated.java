package com.fujentopj.fujento.module.users.domain.event;

import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;

import java.time.Instant;
import java.util.Objects;

/**
 * Evento di dominio interno emesso quando un profilo utente viene aggiornato.
 * NON UTILIZZATO IN QUANTO I COMPORTAMENTI SONO SINGOLI EVENTI
 */
public record UserProfileUpdated(
        UserId userId,
        String updatedBy,
        Instant occurredAt
) implements DomainEvent{
    @Override
    public UserId agggregateId() {
        return userId;
    }

    @Override
    public Instant occuredAt() {
        return occurredAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserProfileUpdated(UserId id, String by, Instant at))) return false;
        return userId.equals(id) && updatedBy.equals(by) && occurredAt.equals(at);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, updatedBy, occurredAt);
    }


    public UserProfileUpdated{
        Objects.requireNonNull(userId, "userId is required");
        Objects.requireNonNull(updatedBy, "updatedBy is required");
        Objects.requireNonNull(occurredAt, "occurredAt is required");
    }
}
