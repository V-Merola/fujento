package com.fujentopj.fujento.module.users.domain.event;

import com.fujentopj.fujento.module.users.domain.event.marker.ModifiedByAware;
import com.fujentopj.fujento.module.users.domain.event.marker.ReasonAware;
import com.fujentopj.fujento.module.users.domain.model.enums.Role;
import com.fujentopj.fujento.module.users.domain.model.snapshot.UserSnapshot;
import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;
import org.apache.catalina.User;

import java.time.Instant;
import java.util.Optional;

public record UserRoleChanged(
        UserSnapshot snapshot,
        Instant occurredAt,
        UserId modifiedBy,
        Optional<String> reason,
        Optional<String> correlationId,
        Optional<String> traceId
) implements DomainEvent, ModifiedByAware, ReasonAware {
    public static UserRoleChanged of(UserSnapshot snapshot, UserId modifiedBy, String reason) {
        return new UserRoleChanged(
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
}
