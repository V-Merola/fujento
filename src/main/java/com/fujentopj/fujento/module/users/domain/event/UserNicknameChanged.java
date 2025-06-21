package com.fujentopj.fujento.module.users.domain.event;

import com.fujentopj.fujento.module.users.domain.model.snapshot.UserSnapshot;
import com.fujentopj.fujento.module.users.domain.model.valueObject.Nickname;
import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;

import java.time.Instant;
import java.util.Optional;

public record UserNicknameChanged(
        UserSnapshot snapshot,
        Instant occurredAt,
        UserId modifiedBy,
        Optional<String> reason,
        Optional<String> correlationId,
        Optional<String> traceId
) implements DomainEvent {
    public static UserNicknameChanged of(UserSnapshot snapshot, UserId modifiedBy, String reason) {
        return new UserNicknameChanged(
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
