package com.fujentopj.fujento.module.users.domain.service.policy;

import com.fujentopj.fujento.module.users.domain.model.enums.UserStatus;
import com.fujentopj.fujento.module.users.domain.model.exception.InvalidUserStateException;

import java.util.Map;
import java.util.Set;

public class DefaultUserStatusTransitionPolicy implements UserStatusTransitionPolicy{

    private static final Map<UserStatus, Set<UserStatus>> allowedTransitions = Map.of(
        UserStatus.ACTIVE, Set.of(UserStatus.DISABLED, UserStatus.BANNED),
        UserStatus.DISABLED, Set.of(UserStatus.ACTIVE, UserStatus.BANNED),
        UserStatus.SUSPENDED, Set.of(UserStatus.ACTIVE, UserStatus.INACTIVE),
        UserStatus.DELETED, Set.of() // STATO TERMINALE
    );


    @Override
    public boolean canTransition(UserStatus from, UserStatus to) {
        return allowedTransitions.getOrDefault(from, Set.of()).contains(to);
    }

    @Override
    public void validate(UserStatus from, UserStatus to) throws InvalidUserStateException {
        if (!canTransition(from, to)) {
            throw new InvalidUserStateException("Transizione di stato non consentita: da " + from + " a " + to);
        }
    }
}
