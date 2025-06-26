package com.fujentopj.fujento.module.users.infrastructure.configuration;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fujentopj.fujento.module.users.domain.model.aggregate.User;
import com.fujentopj.fujento.module.users.domain.model.valueObject.Email;
import com.fujentopj.fujento.module.users.domain.model.valueObject.Nickname;
import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;
import com.fujentopj.fujento.module.users.domain.model.exception.InvalidUserStateException;
import com.fujentopj.fujento.module.users.domain.model.enums.Role;
import com.fujentopj.fujento.module.users.domain.model.enums.UserStatus;
import com.fujentopj.fujento.module.users.port.out.UserRepository;
import com.fujentopj.fujento.module.users.domain.service.policy.RoleAssignmentPolicy;
import com.fujentopj.fujento.module.users.domain.service.policy.UserPermissionPolicy;
import com.fujentopj.fujento.module.users.domain.service.policy.UserStatusTransitionPolicy;

/**
 * Configurazione di bean stub/in-memory per far partire l'applicazione
 * anche se non hai ancora un adapter reale per persistence o policy complesse.
 */
@Configuration
public class DomainStubConfig {

    /**
     * In-memory implementation di UserRepository per testare il dominio.
     * NOTA: questo è solo per sviluppo iniziale. Successivamente implementerai
     * un adapter reale (JPA, ecc.) che sostituirà questo bean.
     */
//    @Bean
//    public UserRepository userRepository() {
//        return new InMemoryUserRepository();
//    }

    /**
     * Stub RoleAssignmentPolicy: consente sempre ogni assegnazione di ruolo.
     * In produzione, sostituisci con bean reale che implementa la logica di business.
     */
    @Bean
    public RoleAssignmentPolicy roleAssignmentPolicy() {
        return new RoleAssignmentPolicy() {
            @Override
            public boolean canAssign(Role currentRole, Role requestedRole) {
                // Permette sempre la transizione di ruolo (solo per sviluppo dominio).
                return true;
            }
        };
    }

    /**
     * Stub UserPermissionPolicy: consente tutte le operazioni di permesso.
     * Metodi di questa interfaccia dipendono dalla definizione nell’application.
     * Qui assumiamo signature simile a: canChangeEmail(User), canChangeStatus(User), ecc.
     */
    @Bean
    public UserPermissionPolicy userPermissionPolicy() {
        return new UserPermissionPolicy() {
            @Override
            public boolean canChangeEmail(User user) {
                return true;
            }
            @Override
            public boolean canChangeRole(User user) {
                return true;
            }
            @Override
            public boolean canChangeStatus(User user) {
                return true;
            }
            @Override
            public boolean canChangeNickname(User user) {
                return true;
            }
            @Override
            public boolean canChangePassword(User user) {
                return true;
            }

            // Se l’interfaccia ha altri metodi, implementali restituendo true o logica minima.
        };
    }

    /**
     * Stub UserStatusTransitionPolicy: consente tutte le transizioni di stato.
     * In produzione, sostituisci con bean reale che verifica le transizioni valide.
     */
    @Bean
    public UserStatusTransitionPolicy userStatusTransitionPolicy() {
        return new UserStatusTransitionPolicy() {
            @Override
            public void validate(UserStatus oldStatus, UserStatus newStatus) throws InvalidUserStateException {
                // Non lancia eccezione: tutte le transizioni sono consentite.
                // Puoi inserire logica minima, es:
                 if (oldStatus == newStatus) throw new InvalidUserStateException("Stesso stato");
            }

            @Override
            public boolean canTransition(UserStatus from, UserStatus to) {
                // Consente sempre la transizione tra stati (solo per sviluppo dominio).
                return true;
            }
        };
    }

    /**
     * InMemoryUserRepository: implementazione fittizia di UserRepository.
     * Mantiene una mappa interna per salvare User in memoria.
     */
//    static class InMemoryUserRepository implements UserRepository {
//
//        // Mappa id -> User
//        private final Map<String, User> store = new ConcurrentHashMap<>();
//
//        @Override
//        public boolean existsByEmail(Email email) {
//            if (email == null) return false;
//            String v = email.getValue();
//            return store.values().stream()
//                    .anyMatch(u -> u.getEmail() != null && v.equalsIgnoreCase(u.getEmail().getValue()));
//        }
//
//        @Override
//        public boolean existsByNickname(Nickname nickname) {
//            if (nickname == null) return false;
//            String v = nickname.getValue();
//            return store.values().stream()
//                    .anyMatch(u -> u.getNickname() != null && v.equalsIgnoreCase(u.getNickname().getValue()));
//        }
//
//        @Override
//        public Optional<User> findById(UserId id) {
//            if (id == null) return Optional.empty();
//            return Optional.ofNullable(store.get(id.getValue()));
//        }
//
//        @Override
//        public Optional<User> findByEmail(Email email) {
//            return Optional.empty();
//        }
//
//        @Override
//        public User save(User user) {
//            if (user == null || user.getId() == null) {
//                throw new IllegalArgumentException("User o UserId nullo non permesso");
//            }
//            // Nota: se salvi un aggregate User che è già esistente, stai sostituendo la referenza.
//            store.put(user.getId().toString(), user);
//            return user;
//        }
//
//        @Override
//        public void delete(User user) {
//            if (user != null && user.getId() != null) {
//                store.remove(user.getId().getValue());
//            }
//        }
//
//        // Se la tua interfaccia UserRepository ha altri metodi, implementali qui.
//    }
}
