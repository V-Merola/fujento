package com.fujentopj.fujento.module.users.application.service;

import com.fujentopj.fujento.module.users.application.command.ChangeUserRoleCommand;
import com.fujentopj.fujento.module.users.application.exception.EntityNotFoundException;
import com.fujentopj.fujento.module.users.domain.model.aggregate.User;
import com.fujentopj.fujento.module.users.domain.service.UserValidator;
import com.fujentopj.fujento.module.users.domain.service.policy.RoleAssignmentPolicy;
import com.fujentopj.fujento.module.users.domain.service.policy.UserPermissionPolicy;
import com.fujentopj.fujento.module.users.port.out.DomainEventPublisher;
import com.fujentopj.fujento.module.users.port.out.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangeUserRoleService {
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final UserPermissionPolicy userPermissionPolicy;
    private final RoleAssignmentPolicy roleAssignmentPolicy;
    private final DomainEventPublisher eventPublisher;

    public ChangeUserRoleService(
            UserRepository userRepository,
            UserValidator userValidator,
            UserPermissionPolicy userPermissionPolicy,
            RoleAssignmentPolicy roleAssignmentPolicy,
            DomainEventPublisher domainEventPublisher) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.userPermissionPolicy = userPermissionPolicy;
        this.roleAssignmentPolicy = roleAssignmentPolicy;
        this.eventPublisher = domainEventPublisher;
    }
    @Transactional
    public void handle(ChangeUserRoleCommand command) {
        // 1. Validazioni applicative superficiali:
        if (command.newRole() == null) {
            throw new IllegalArgumentException("newRole obbligatorio");
        }
        // reason max length, correlationId format, ecc., se serve:
        command.reason().ifPresent(r -> {
            if (r.length() > 255) {
                throw new IllegalArgumentException("Reason troppo lungo");
            }
        });
        // Carica l’aggregate
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato con id: " + command.userId()));

        // Verifica permessi: UserPermissionPolicy iniettato (implementazione in application/security)
        if(!userPermissionPolicy.canChangeRole(user)){
            throw new IllegalStateException("Permessi insufficienti per cambiare il ruolo dell'utente");
        }

        // Validazioni di formato/regole via UserValidator
        userValidator.validateRoleAssignment(user.getRole(), command.newRole());

        // Invoca il metodo di dominio
        user.changeRole(
                command.newRole(),
                command.modifiedBy(),
                command.reason(),
                roleAssignmentPolicy
        );
        // Salva l'aggregate
        userRepository.save(user);

        // Pubblica eventi generati
        eventPublisher.publishAll(user.getDomainEvents());

        //Reset degli eventi di dominio
        user.clearDomainEvents();
    }

}
