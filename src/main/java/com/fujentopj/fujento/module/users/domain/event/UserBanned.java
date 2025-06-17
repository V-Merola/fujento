package com.fujentopj.fujento.module.users.domain.event;

import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;

import java.time.Instant;

public record UserBanned(
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

    // No additional methods or fields are needed for this event.
    // It simply represents a user being banned with the necessary information.

}
