package com.fujentopj.fujento.module.users.domain.service;

import com.fujentopj.fujento.module.users.domain.model.enums.Role;
import com.fujentopj.fujento.module.users.domain.model.exception.InvalidRoleAssignmentException;
import com.fujentopj.fujento.module.users.domain.model.valueObject.Email;
import com.fujentopj.fujento.module.users.domain.model.valueObject.Nickname;

/**
 * Domain Service per validare i dati dell'utente.
 * Stateles – centralizza la logica di validazione del dominio utente.
 */
public interface UserValidator {
    /**
     * Valida se un'email è corretta e non già utilizzata
     * Lancia { @link IllegalArgumentException} se non valida o già in uso.
     */
    void validateEmail(Email email);

    /**
     * Valida la correttezza del nickname (lunghezza, formato, ecc.).
     * Lancia {@linkInvalidNicknameException} se non valido.
     */
    void validateNickname(Nickname nickname);

    /**
     * Controlla se è valido assegnare un certo ruolo a un utente.
     * Utile durante la registrazione o modifiche da admin.
     */
    void validateRoleAssignment(Role currentRole, Role requestedRole) throws InvalidRoleAssignmentException;
}
