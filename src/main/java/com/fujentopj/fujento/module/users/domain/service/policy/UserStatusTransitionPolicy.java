package com.fujentopj.fujento.module.users.domain.service.policy;

import com.fujentopj.fujento.module.users.domain.model.enums.UserStatus;
import com.fujentopj.fujento.module.users.domain.model.exception.InvalidUserStateException;

public interface UserStatusTransitionPolicy {
    void validate(UserStatus from, UserStatus to) throws InvalidUserStateException;
    boolean canTransition(UserStatus from, UserStatus to);
}
