package com.fujentopj.fujento.module.users.domain.event;

import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;

import java.time.Instant;

public record UserPasswordChanged(
        UserId userId,
        Instant occurredAt
) implements DomainEvent {
    @Override
    public UserId aggregateId() {
        return userId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
