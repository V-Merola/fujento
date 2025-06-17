package com.fujentopj.fujento.module.users.domain.service;

import com.fujentopj.fujento.module.users.domain.model.enums.Role;

import java.lang.FunctionalInterface;
/**
 * Regola per stabilire se un utente può cambiare il proprio ruolo.
 */
@FunctionalInterface
public interface RoleAssignmentPolicy {
    /**
     * Verifica se è possibile assegnare il nuovo ruolo.
     *
     * @param current Il ruolo attuale dell’utente (può essere null se è una registrazione).
     * @param requested Il ruolo che si desidera assegnare.
     * @return true se il ruolo può essere assegnato, false altrimenti.
     */
    boolean canAssign(Role current, Role requested);
}
