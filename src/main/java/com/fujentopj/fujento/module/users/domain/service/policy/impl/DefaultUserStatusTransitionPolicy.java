package com.fujentopj.fujento.module.users.domain.service.policy.impl;

import com.fujentopj.fujento.module.users.domain.model.enums.UserStatus;
import com.fujentopj.fujento.module.users.domain.model.exception.InvalidUserStateException;
import com.fujentopj.fujento.module.users.domain.service.policy.UserStatusTransitionPolicy;

import java.util.Map;
import java.util.Set;

public class DefaultUserStatusTransitionPolicy implements UserStatusTransitionPolicy {

    private static final Map<UserStatus, Set<UserStatus>> allowedTransitions = Map.of(
            UserStatus.ACTIVE, Set.of(UserStatus.DISABLED, UserStatus.BANNED, UserStatus.ARCHIVED),
            UserStatus.DISABLED, Set.of(UserStatus.ACTIVE, UserStatus.BANNED, UserStatus.ARCHIVED),
            UserStatus.BANNED, Set.of(), // o lasci vuoto se non vuoi archiviare i bannati
            UserStatus.ARCHIVED, Set.of() // stato terminale
    );

    @Override
    public boolean canTransition(UserStatus from, UserStatus to) {
        return allowedTransitions.getOrDefault(from, Set.of()).contains(to);
    }

    @Override
    public void validate(UserStatus from, UserStatus to) throws InvalidUserStateException {
        //Protezione specifica per vietare archiviazione di utenti bannati, gia dichiarata in Map-> BANNED.Set.of()
        if(from == UserStatus.BANNED && to == UserStatus.ARCHIVED) {
            throw new InvalidUserStateException("Non è consentito archiviare utenti bannati.");
        }

        if (!canTransition(from, to)) {
            throw new InvalidUserStateException("Transizione di stato non consentita: da " + from + " a " + to);
        }
    }
}
