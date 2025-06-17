package com.fujentopj.fujento.module.users.domain.service.rule;

import com.fujentopj.fujento.module.users.domain.model.aggregate.User;
import com.fujentopj.fujento.module.users.domain.model.enums.Role;
import com.fujentopj.fujento.module.users.domain.service.BusinessRule;

/**
 * Regola di business che impedisce di bannare un utente con ruolo ADMIN.
 * Se l'utente ha il ruolo ADMIN, la regola non è soddisfatta.
 */

public class CannotBanAdminRule implements BusinessRule {

    private  final User user;

    public CannotBanAdminRule(User user){
        this.user = user;
    }
    @Override
    public boolean isSatisfied() {
        return !Role.ADMIN.equals(user.getRole());
    }

    @Override
    public String message() {
        return "Un utente con ruolo ADMIN non può essere bannato.";
    }
}
