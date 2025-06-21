package com.fujentopj.fujento.module.users.application.service;

import com.fujentopj.fujento.module.users.application.command.ChangeUserStatusCommand;
import com.fujentopj.fujento.module.users.application.exception.EntityNotFoundException;
import com.fujentopj.fujento.module.users.domain.model.aggregate.User;
import com.fujentopj.fujento.module.users.domain.service.policy.UserPermissionPolicy;
import com.fujentopj.fujento.module.users.domain.service.policy.UserStatusTransitionPolicy;
import com.fujentopj.fujento.module.users.port.out.DomainEventPublisher;
import com.fujentopj.fujento.module.users.port.out.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ChangeUserStatusService {
    private final UserRepository userRepository;
    private final UserPermissionPolicy permissionPolicy;
    private final UserStatusTransitionPolicy statusTransitionPolicy;
    private final DomainEventPublisher eventPublisher;

    public ChangeUserStatusService(
            UserRepository userRepository,
            UserPermissionPolicy permissionPolicy,
            UserStatusTransitionPolicy statusTransitionPolicy,
            DomainEventPublisher eventPublisher
    ) {
        this.userRepository = userRepository;
        this.permissionPolicy = permissionPolicy;
        this.statusTransitionPolicy = statusTransitionPolicy;
        this.eventPublisher = eventPublisher;
    }

    public void handle(ChangeUserStatusCommand command){
        // 1. Validazioni applicative superficiali (null check, length reason, etc.)
        if (command.newStatus() == null) {
            throw new IllegalArgumentException("newStatus obbligatorio");
        }
        command.reason().ifPresent(r -> {
            if (r.length() > 255) {
                throw new IllegalArgumentException("Reason troppo lungo");
            }
        });
        // 2. Carica l’aggregate
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato con id: " + command.userId()));

        // 3. Invoca il metodo di dominio
        user.changeStatus(
                command.newStatus(),
                command.modifiedBy(),
                command.reason(),
                statusTransitionPolicy,
                permissionPolicy
        );

        // 4. Persisti l'aggregate
        userRepository.save(user);

        // 5. Pubblica eventi generati
        eventPublisher.publishAll(user.getDomainEvents());
        user.clearDomainEvents();
    }
}

