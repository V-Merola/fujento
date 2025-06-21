package com.fujentopj.fujento.module.users.domain.event;

import com.fujentopj.fujento.module.users.domain.model.snapshot.UserSnapshot;
import com.fujentopj.fujento.module.users.domain.model.valueObject.Email;
import com.fujentopj.fujento.module.users.domain.model.valueObject.Nickname;
import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;

import java.time.Instant;
import java.util.Optional;

/**
 * Evento di dominio emesso quando un utente viene registrato.
 */
public record UserRegistered(
        UserSnapshot snapshot,
        Instant occurredAt

) implements DomainEvent{
    @Override
    public UserId aggregateId() {
        return snapshot.id();
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }
}
