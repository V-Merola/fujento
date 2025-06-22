// src/main/java/com/fujentopj/fujento/module/users/adapter/out/validation/UserValidatorImpl.java
package com.fujentopj.fujento.module.users.adapter.out.validation;

import org.springframework.stereotype.Component;
import com.fujentopj.fujento.module.users.domain.service.UserValidator;
import com.fujentopj.fujento.module.users.domain.model.valueObject.Email;
import com.fujentopj.fujento.module.users.domain.model.valueObject.Nickname;
import com.fujentopj.fujento.module.users.domain.model.enums.Role;
//import com.fujentopj.fujento.module.users.domain.model.exception.DuplicateEmailException;
//import com.fujentopj.fujento.module.users.domain.model.exception.InvalidNicknameException;
import com.fujentopj.fujento.module.users.domain.model.exception.InvalidRoleAssignmentException;
import com.fujentopj.fujento.module.users.port.out.UserRepository;
import com.fujentopj.fujento.module.users.domain.service.policy.RoleAssignmentPolicy;
/**
 * Implementazione di UserValidator: delega a VO per validazione di formato,
 * a UserRepository per unicità email/nickname, e a RoleAssignmentPolicy se serve
 * (o direttamente lancia eccezione se la regola di ruolo è gestita qui).
 */

//Quando componi ChangeUserEmailService, definisci costruttore:
//
//public ChangeUserEmailService(UserRepository userRepository,
//                              UserValidator userValidator,
//                              UserPermissionPolicy permissionPolicy,
//                              DomainEventPublisher eventPublisher) { ... }
//Spring inietterà UserValidatorImpl come UserValidator.

@Component
public class UserValidatorImpl implements UserValidator {

    private final UserRepository userRepository;
    // Se hai una policy di business esterna per ruoli:
    private final RoleAssignmentPolicy roleAssignmentPolicy;

    public UserValidatorImpl(UserRepository userRepository,
                             RoleAssignmentPolicy roleAssignmentPolicy) {
        this.userRepository = userRepository;
        this.roleAssignmentPolicy = roleAssignmentPolicy;
    }

    @Override
    public void validateEmail(Email email) {
        // Il VO Email.of(...) ha già validato sintassi quando è stato creato.
        // Qui controlliamo unicità:
        if (userRepository.existsByEmail(email)) {
           // throw new DuplicateEmailException("Email già in uso: " + email.getValue());
            throw new RuntimeException("Email già in uso: " + email.getValue());
        }
    }

    @Override
    public void validateNickname(Nickname nickname) {
        // Il VO Nickname.of(...) ha già validato formato.
        // Se vuoi unicità nickname:
        // if (userRepository.existsByNickname(nickname)) throw new DuplicateNicknameException(...);
        // Altrimenti, nessun controllo extra.
        // (Se non serve unicità, lascia vuoto o esegui ulteriori regole di business).
    }

    @Override
    public void validateRoleAssignment(Role currentRole, Role requestedRole) throws InvalidRoleAssignmentException {
        // Se la regola di business di cambiamento ruolo è gestita altrove (in domain o policy),
        // puoi delegare a RoleAssignmentPolicy:
        if (!roleAssignmentPolicy.canAssign(currentRole, requestedRole)) {
            throw new InvalidRoleAssignmentException("Transizione di ruolo non consentita: da "
                    + currentRole + " a " + requestedRole);
        }
    }
}
