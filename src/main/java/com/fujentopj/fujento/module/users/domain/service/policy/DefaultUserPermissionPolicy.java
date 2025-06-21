package com.fujentopj.fujento.module.users.domain.service.policy;

import com.fujentopj.fujento.module.users.domain.model.aggregate.User;
import com.fujentopj.fujento.module.users.domain.model.enums.Role;
import com.fujentopj.fujento.module.users.domain.model.enums.UserStatus;
import com.fujentopj.fujento.module.users.domain.model.exception.InvalidUserStateException;
import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;

public class DefaultUserPermissionPolicy implements UserPermissionPolicy{
    private final Role currentUserRole;
    private final String currentUserId;

    public DefaultUserPermissionPolicy(Role currentUserRole, String currentUserId) {
        this.currentUserRole = currentUserRole;
        this.currentUserId = currentUserId;
    }

    @Override
    public boolean canChangeEmail(User target) {
        //l'utente passato deve essere o l'utente corrente o un admin e non deve essere bannato o disabilitato
        return (isSelf(target) || currentUserRole == Role.ADMIN) && !isBannedOrDisabled(target);
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
        return currentUserRole == Role.ADMIN && !isSelf(target) && !isBannedOrDisabled(target);
    }

    @Override
    public boolean canChangeStatus(User target) {
        return currentUserRole == Role.ADMIN && !isSelf(target);
    }

    // Validazione dello stato dell'utente
    private boolean isSelf(User target) {
        return target.getId().value().toString().equals(currentUserId);
    }
    // Controllo se l'utente è bannato o disabilitato
    private boolean isBannedOrDisabled(User target) {
        return target.getStatus() == UserStatus.BANNED || target.getStatus() == UserStatus.DISABLED;
    }

}
