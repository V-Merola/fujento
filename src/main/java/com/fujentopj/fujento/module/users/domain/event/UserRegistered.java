package com.fujentopj.fujento.module.users.domain.event;

import com.fujentopj.fujento.module.users.domain.model.snapshot.UserSnapshot;
import com.fujentopj.fujento.module.users.domain.model.valueObject.Email;
import com.fujentopj.fujento.module.users.domain.model.valueObject.Nickname;
import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;

import java.time.Instant;
import java.util.Optional;

import static com.nimbusds.openid.connect.sdk.claims.LogoutTokenClaimsSet.EVENT_TYPE;

/**
 * Evento di dominio emesso quando un utente viene registrato.
 */
public record UserRegistered(
        String EVENT_TYPE ,
        UserSnapshot snapshot,
        Instant occurredAt

        ) implements DomainEvent{
    public static UserRegistered of(UserSnapshot snapshot) {

        return new UserRegistered(
                "",
                snapshot,
                Instant.now()
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
