package com.fujentopj.fujento.module.users.domain.command;

import com.fujentopj.fujento.module.users.domain.model.enums.Role;
import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;

import java.time.Instant;
import java.util.Objects;

 public record ChangeUserRoleCommand(
        UserId userId,
        Role newRole,
        UserId modifiedBy,
        String reason,
        Instant occurredAt
) {
   public ChangeUserRoleCommand {
       Objects.requireNonNull(userId);
       Objects.requireNonNull(newRole);
       Objects.requireNonNull(modifiedBy);
       Objects.requireNonNull(occurredAt);
        }
}
