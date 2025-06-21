package com.fujentopj.fujento.module.users.application.command;

import com.fujentopj.fujento.module.users.domain.model.enums.UserStatus;
import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;

import java.time.Instant;
import java.util.Optional;

public record ChangeUserStatusCommand(
        UserId userId,
        UserStatus newStatus,
        UserId modifiedBy,
        Instant occurredAt,
        Optional<String> reason,
        Optional<String> correlationId,
        Optional<String> traceId,
        long expectedVersion
) {
    public ChangeUserStatusCommand {
        if (userId == null) {
            throw new IllegalArgumentException("userId non può essere null");
        }
        if (newStatus == null) {
            throw new IllegalArgumentException("newStatus non può essere null");
        }
        if (modifiedBy == null) {
            throw new IllegalArgumentException("modifiedBy non può essere null");
        }
        if (occurredAt == null) {
            throw new IllegalArgumentException("occurredAt non può essere null");
        }
        // reason, correlationId, traceId opzionali
    }
//    public static ChangeUserStatusCommand of(UserStatus status, UserId by) {
//        return new ChangeUserStatusCommand(status, by, Instant.now(), Optional.empty());
//    }
//
//    public static ChangeUserStatusCommand of(UserStatus status, UserId by, String reason) {
//        return new ChangeUserStatusCommand(status, by, Instant.now(), Optional.of(reason));
//    }
    /**
     * Factory di comodo senza reason/ids:
     */
    public static ChangeUserStatusCommand of(
            UserId userId,
            UserStatus newStatus,
            UserId modifiedBy
    ) {
        return new ChangeUserStatusCommand(
                userId,
                newStatus,
                modifiedBy,
                Instant.now(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                0L // Versione attesa, di solito 0 per nuovi comandi
        );
    }

    /**
     * Factory completo:
     */
    public static ChangeUserStatusCommand of(
            UserId userId,
            UserStatus newStatus,
            UserId modifiedBy,
            Instant occurredAt,
            String reason,
            String correlationId,
            String traceId,
            long expectedVersion
    ) {
        return new ChangeUserStatusCommand(
                userId,
                newStatus,
                modifiedBy,
                occurredAt != null ? occurredAt : Instant.now(),
                Optional.ofNullable(reason),
                Optional.ofNullable(correlationId),
                Optional.ofNullable(traceId),
                expectedVersion
        );
    }
}
