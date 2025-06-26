package com.fujentopj.fujento.module.users.application.service;

import com.fujentopj.fujento.module.users.application.command.ChangeUserEmailCommand;
import com.fujentopj.fujento.module.users.application.exception.EntityNotFoundException;
import com.fujentopj.fujento.module.users.port.in.ChangeUserEmailUseCase;
import com.fujentopj.fujento.module.users.domain.model.aggregate.User;
import com.fujentopj.fujento.module.users.domain.model.exception.InvalidUserStateException;
import com.fujentopj.fujento.module.users.domain.service.UserValidator;
import com.fujentopj.fujento.module.users.domain.service.policy.UserPermissionPolicy;
import com.fujentopj.fujento.module.users.port.out.DomainEventPublisher;
import com.fujentopj.fujento.module.users.port.out.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/*
*Nota Architetturale: L'utilizzo di @Service andrebbe in adapter per mantedere "Puro" il layer Application:
* In un'architettura a strati, il layer Application dovrebbe essere responsabile della logica di coordinamento e orchestrazione,
* mentre il layer Adapter (o Infrastructure) si occupa delle implementazioni concrete dei servizi, come la persistenza dei dati o la comunicazione con sistemi esterni.
* In questo caso, ChangeUserEmailService è un servizio di dominio che implementa la logica di cambiamento dell'email dell'utente.
* Tuttavia, poiché è annotato con @Service, potrebbe essere considerato parte del layer Adapter.
* Per mantenere il layer Application "puro", potresti considerare di rimuovere l'annotazione @Service e gestire l'istanza di ChangeUserEmailService tramite Dependency Injection nel layer Adapter.
* Questo approccio ti permetterebbe di mantenere la separazione delle responsabilità e di evitare che il layer Application dipenda direttamente da implementazioni concrete.
*
* ❌ SCENARIO "PURISTA" (DDD + Architettura Hexagonale 100%)
*portare la separazione al massimo:

// application.service
public class ChangeUserEmailService implements ChangeUserEmailUseCase {
    ...
}
*

Niente @Service, @Transactional, ecc. nel package application

L’annotazione @Service va in un adapter dedicato, ad esempio:

// adapter.in.spring
@Service
public class SpringChangeUserEmailServiceAdapter implements ChangeUserEmailUseCase {
    private final ChangeUserEmailService internal;

    public void handle(...) {
        internal.handle(...);
    }
}
È un livello di purezza che consente anche il riutilizzo in contesti non-Spring, come:

CLI Java puri

AWS Lambda

Altri framework (Micronaut, Quarkus, ecc.)

🔧 Ma è anche più complesso e raramente necessario se lavori esclusivamente con Spring.
*/

@Service
public class ChangeUserEmailService implements ChangeUserEmailUseCase {
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

        if (!userPermissionPolicy.canChangeEmail(user)) {
            throw new InvalidUserStateException("Permessi insufficienti per cambiare l'email.");
        }
        userValidator.validateEmail(command.newEmail());

        user.changeEmail(
                command.newEmail(),
                command.modifiedBy(),
                command.reason()
        );
        userRepository.save(user);
        eventPublisher.publishAll(user.getDomainEvents());
        // Reset the domain events after publishing
        user.clearDomainEvents();

    }

}
