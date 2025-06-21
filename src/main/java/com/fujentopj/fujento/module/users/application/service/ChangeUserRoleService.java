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
        // 2. Carica l’aggregate
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato con id: " + command.userId()));

        // 3. Invoca il metodo di dominio
        user.changeRole(
                command.newRole(),
                command.modifiedBy(),
                command.reason(),
                userValidator,
                roleAssignmentPolicy,
                userPermissionPolicy
        );

        // 4. Persisti
        userRepository.save(user);

        // 5. Pubblica eventi generati
        eventPublisher.publishAll(user.getDomainEvents());

        // 6. Reset degli eventi di dominio
        user.clearDomainEvents();
    }

}
