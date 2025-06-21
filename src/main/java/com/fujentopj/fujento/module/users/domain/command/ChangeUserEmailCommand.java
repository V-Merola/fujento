package com.fujentopj.fujento.module.users.domain.command;

import com.fujentopj.fujento.module.users.domain.model.valueObject.Email;
import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;

import java.time.Instant;
import java.util.Optional;

public record ChangeUserEmailCommand(
    UserId userId,
    Email newEmail,
    UserId modifiedBy,
    Instant occurredAt,
    Optional<String> reason,
    Optional<String> correlationId,
    Optional<String> traceId,
    long expectedVersion  // se si gestisce optimistic locking
) {}
