package com.fujentopj.fujento.module.users.domain.event;

import com.fujentopj.fujento.module.users.domain.model.valueObject.Email;
import com.fujentopj.fujento.module.users.domain.model.valueObject.Nickname;
import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;

import java.time.Instant;

/**
 * Evento di dominio emesso quando un utente viene registrato.
 */
public record UserRegistered(
        UserId userId,
        Email email,
        Nickname nickname,
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
}
