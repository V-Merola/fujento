package com.fujentopj.fujento.module.users.application.command;

import com.fujentopj.fujento.module.users.domain.model.enums.Role;
import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public record ChangeUserRoleCommand(
        UserId userId,
        Role newRole,
        UserId modifiedBy,
        Instant occurredAt,
        Optional<String> reason,
        Optional<String> correlationId,
        Optional<String> traceId,
        long expectedVersion
) {
   public ChangeUserRoleCommand {
       Objects.requireNonNull(userId);
       Objects.requireNonNull(newRole);
       Objects.requireNonNull(modifiedBy);
       Objects.requireNonNull(occurredAt);
   }
    public static ChangeUserRoleCommand of(UserId userId, Role newRole, UserId modifiedBy) {
         return new ChangeUserRoleCommand(
                userId,
                newRole,
                modifiedBy,
                Instant.now(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                0
         );
    }
}
