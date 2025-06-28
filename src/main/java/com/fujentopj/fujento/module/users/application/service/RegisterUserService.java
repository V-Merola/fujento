package com.fujentopj.fujento.module.users.application.service;

import com.fujentopj.fujento.module.users.application.command.RegisterUserCommand;
import com.fujentopj.fujento.module.users.domain.model.aggregate.User;
import com.fujentopj.fujento.module.users.domain.model.enums.Role;
import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;
import com.fujentopj.fujento.module.users.domain.service.UserValidator;
import com.fujentopj.fujento.module.users.port.in.RegisterUserUseCase;
import com.fujentopj.fujento.module.users.port.out.DomainEventPublisher;
import com.fujentopj.fujento.module.users.port.out.PasswordHasherPort;
import com.fujentopj.fujento.module.users.port.out.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegisterUserService implements RegisterUserUseCase {

   private final UserRepository userRepository;
   private final UserValidator userValidator;
   private final DomainEventPublisher eventPublisher;
   private final PasswordHasherPort passwordHasher;


    public RegisterUserService(UserRepository userRepository, UserValidator userValidator, DomainEventPublisher eventPublisher, PasswordHasherPort passwordHasher) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.eventPublisher = eventPublisher;
        this.passwordHasher = passwordHasher;
    }
    @Override
    public User register(RegisterUserCommand command) {

        //if (!userValidator.validateEmail(command.email())){return null;}
        userValidator.validateEmail(command.email());

        userValidator.validateNickname(command.nickname());

        var hashedPassword = passwordHasher.hash(command.rawPassword());
        var userId = UserId.create();
        var user = User.register(
                userId,
                command.email(),
                hashedPassword,
                command.nickname(),
                Role.USER
        );
        userRepository.save(user);
        eventPublisher.publishAll(
                user.getDomainEvents()  // Assuming pullDomainEvents returns a collection of events
        );


        user.clearDomainEvents(); // Clear events after publishing
        return user;
    }

}
