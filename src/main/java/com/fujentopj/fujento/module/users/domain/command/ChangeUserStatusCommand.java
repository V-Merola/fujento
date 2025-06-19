package com.fujentopj.fujento.module.users.domain.command;

import com.fujentopj.fujento.module.users.domain.model.enums.UserStatus;
import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;

import java.time.Instant;
import java.util.Optional;

public record ChangeUserStatusCommand(
        UserStatus newStatus,
        UserId modifiedBy,
        Instant occurredAt,
        Optional<String> reason
) {
    public static ChangeUserStatusCommand of(UserStatus status, UserId by) {
        return new ChangeUserStatusCommand(status, by, Instant.now(), Optional.empty());
    }

    public static ChangeUserStatusCommand of(UserStatus status, UserId by, String reason) {
        return new ChangeUserStatusCommand(status, by, Instant.now(), Optional.of(reason));
    }
}
