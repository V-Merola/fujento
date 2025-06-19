package com.fujentopj.fujento.module.users.domain.service.policy;

import com.fujentopj.fujento.module.users.domain.model.enums.Role;

/**
 * Implementazione predefinita di RoleAssignmentPolicy.
 * Esempio: non si può assegnare ADMIN direttamente, e non si può rimuovere a un ADMIN.
 */
public class DefaultRoleAssignmentPolicy implements RoleAssignmentPolicy {
    @Override
    public boolean canAssign(Role current, Role requested) {
        if (current == Role.ADMIN && requested != Role.ADMIN) {
            return false; // Non si può declassare un admin
        }

        return current != null || requested != Role.ADMIN; // Non si può assegnare direttamente ADMIN
    }
}
