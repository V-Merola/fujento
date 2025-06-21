package com.fujentopj.fujento.module.users.domain.service.policy;

import com.fujentopj.fujento.module.users.domain.model.aggregate.User;
import com.fujentopj.fujento.module.users.domain.model.enums.Role;
import com.fujentopj.fujento.module.users.domain.model.enums.UserStatus;

public class DefaultUserPermissionPolicy implements UserPermissionPolicy{
    private final Role currentUserRole;
    private final String currentUserId;

    public DefaultUserPermissionPolicy(Role currentUserRole, String currentUserId) {
        this.currentUserRole = currentUserRole;
        this.currentUserId = currentUserId;
    }

    @Override
    public boolean canChangeEmail() {
        return currentUserRole == Role.ADMIN || currentUserRole == Role.USER;
    }

    @Override
    public boolean canChangeNickname(User target) {
        return isSelf(target) || currentUserRole == Role.ADMIN;
    }

    @Override
    public boolean canChangePassword(User target) {
        return isSelf(target) && !isBannedOrDisabled(target);
    }

    @Override
    public boolean canChangeRole(User target) {
        return currentUserRole == Role.ADMIN && !isSelf(target);
    }

    @Override
    public boolean canChangeStatus(User target) {
        return currentUserRole == Role.ADMIN && !isSelf(target);
    }

    private boolean isSelf(User target) {
        return target.getId().value().toString().equals(currentUserId);
    }

    private boolean isBannedOrDisabled(User target) {
        return target.getStatus() == UserStatus.BANNED || target.getStatus() == UserStatus.DISABLED;
    }
}
