package com.fujentopj.fujento.module.users.application.service;

import com.fujentopj.fujento.module.users.application.command.ChangeUserEmailCommand;
import com.fujentopj.fujento.module.users.application.exception.EntityNotFoundException;
import com.fujentopj.fujento.module.users.domain.model.aggregate.User;
import com.fujentopj.fujento.module.users.domain.service.UserValidator;
import com.fujentopj.fujento.module.users.domain.service.policy.UserPermissionPolicy;
import com.fujentopj.fujento.module.users.port.out.DomainEventPublisher;
import com.fujentopj.fujento.module.users.port.out.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangeUserEmailService {
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final UserPermissionPolicy userPermissionPolicy;
    private final DomainEventPublisher eventPublisher;

    public ChangeUserEmailService(UserRepository userRepository, UserValidator userValidator, UserPermissionPolicy userPermissionPolicy, DomainEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.userPermissionPolicy = userPermissionPolicy;

        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void handle(ChangeUserEmailCommand command) {
        User user = userRepository.findById(command.userId()).orElseThrow(
                () ->new EntityNotFoundException("User not found with id: " + command.userId().value())
        );

        user.changeEmail(
                command.newEmail(),
                command.modifiedBy(),
                command.reason(),
                userValidator,
                userPermissionPolicy
        );
        userRepository.save(user);
        eventPublisher.publishAll(user.getDomainEvents());
        // Reset the domain events after publishing
        user.clearDomainEvents();

    }

}
