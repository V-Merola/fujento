package com.fujentopj.fujento.module.users.domain.event;

import com.fujentopj.fujento.module.users.domain.model.enums.Role;
import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;

import java.time.Instant;

public record UserRoleChanged(
        UserId userId,
        Role newRole,
        UserId modifiedBy,
        Instant occurredAt,
        String reason
) implements DomainEvent {

    @Override
    public UserId agggregateId() {
        return userId;
    }

    @Override
    public Instant occuredAt() {
        return occurredAt;
    }
}
