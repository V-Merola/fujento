package com.fujentopj.fujento.module.users.domain.event;

import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;

import java.time.Instant;
import java.util.Optional;

public record UserDeleted (
        UserId userId,
        Instant occurredAt,
        UserId modifiedBy,
        Optional<String> reason
) implements DomainEvent {

    @Override
    public UserId agggregateId() {
        return userId;
    }

    @Override
    public Instant occuredAt() {
        return occurredAt;
    }

    // Additional methods or logic can be added here if needed
}

