package com.fujentopj.fujento.module.users.domain.event;

import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;

import java.time.Instant;

public record UserActivated(
        UserId userId,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public UserId agggregateId() {
        return userId;
    }

    @Override
    public Instant occuredAt() {
        return occurredAt;
    }

    // No additional methods are needed as this is a simple event record.
    // The record automatically provides the necessary methods for accessing the fields.
}
