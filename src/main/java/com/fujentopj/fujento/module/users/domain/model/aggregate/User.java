package com.fujentopj.fujento.module.users.domain.model.aggregate;


import com.fujentopj.fujento.module.users.domain.command.ChangeUserRoleCommand;
import com.fujentopj.fujento.module.users.domain.command.ChangeUserStatusCommand;
import com.fujentopj.fujento.module.users.domain.event.*;
import com.fujentopj.fujento.module.users.domain.model.enums.Role;
import com.fujentopj.fujento.module.users.domain.model.enums.UserStatus;
import com.fujentopj.fujento.module.users.domain.model.exception.InvalidUserStateException;
import com.fujentopj.fujento.module.users.domain.model.valueObject.*;
import com.fujentopj.fujento.module.users.domain.service.UserValidator;
import com.fujentopj.fujento.module.users.domain.service.policy.RoleAssignmentPolicy;
import com.fujentopj.fujento.module.users.domain.service.policy.UserStatusTransitionPolicy;
import com.fujentopj.fujento.module.users.domain.service.rule.CannotBanAdminRule;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import java.util.Collections;
import java.util.Objects;


/**
 * Aggregate Root che rappresenta un utente nel sistema.
 *1.Incapsula tutti gli attributi e i metodi che definiscono un utente.
 * 2.Usa userId come identificatore unico.
 * 3.Contiene Role, UserStatus e SportPreference
 * 4.Metodi di comportamento (no setter)
 * 5.Nessuna logica tecnica(jpa,spring,IO, ecc.)
 * 6.Costruttore protetto + factory statico (register(...))
 * 7.Immutabilità controllata, no mutazioni dirette
 */

public class User {

    private final UserId id;
    private Email email;
    private HashedPassword password;
    private Nickname nickname;
    private Role role;
    private UserStatus status;

    private long version; //gestito da Hibernate (@Version) nel layer persistence

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private boolean dirty = false;

    // ============================
    // Factory Method
    // ============================
    public static User register(
            UserId id,
            Email email,
            HashedPassword password,
            Nickname nickname,
            Role role,
            UserValidator validator
    ) {
        validator.validateEmail(email);
        validator.validateNickname(nickname);
        validator.validateRoleAssignment(null, role);

        User user = new User(id, email, password, nickname, role, UserStatus.ACTIVE);
        //user.registerEvent(new UserRegistered(id.value(), email.value(), nickname.value(), Instant.now()));
        user.registerEvent(new UserRegistered(
                id,
                email,
                nickname,
                Instant.now()
        ));
        return user;
    }

    // ============================
    // Costruttori (ORM-friendly)
    // ============================
    protected User(
            UserId id,
            Email email,
            HashedPassword password,
            Nickname nickname,
            Role role,
            UserStatus status
    ) {
        this.id = Objects.requireNonNull(id);
        this.email = Objects.requireNonNull(email);
        this.password = Objects.requireNonNull(password);
        this.nickname = Objects.requireNonNull(nickname);
        this.role = Objects.requireNonNull(role);
        this.status = Objects.requireNonNull(status);
    }

    protected User() {
        this.id = null; // Per ORM
    }

    // ============================
    // Comportamenti di dominio
    // ============================

    public void changeEmail(Email newEmail, UserValidator validator) {
        validator.validateEmail(newEmail);

        mutateIfChanged(
                this.email,
                newEmail,
                (e) -> this.email = e,
                new UserEmailChanged(
                        id,
                        newEmail,
                        Instant.now()
                )
        );
        
    }

    public void changeNickname(Nickname newNickname, UserValidator validator) {
        validator.validateNickname(newNickname);

        mutateIfChanged(
                this.nickname,
                newNickname,
                (n) -> this.nickname = n,
                new UserNicknameChanged(
                        id,
                        newNickname,
                        Instant.now()
                )
        );
    }

    public void changePassword(HashedPassword newPassword) throws InvalidUserStateException {
        Objects.requireNonNull(newPassword, "Password non può essere null");

        if (this.status == UserStatus.BANNED || this.status == UserStatus.DISABLED) {
            throw new InvalidUserStateException("Utente bannato o disattivato: non può cambiare password.");
        }

        mutateIfChanged(
                this.password,
                newPassword,
                (hp) -> this.password = hp,
                new UserPasswordChanged(
                        id,
                        Instant.now()
                )
        );
    }
    //POTREBBE ESSERE SPLITTATO IN promoteTo e demoteTo PER CHIAREZZA SEMANTICA
    public void changeRole(ChangeUserRoleCommand command, RoleAssignmentPolicy policy) throws InvalidUserStateException{
        if(!policy.canAssign(this.role, command.newRole())) {
            throw new InvalidUserStateException("Cambio ruolo non consentito: " + command.newRole());
        }

        mutateIfChanged(
                this.role,
                command.newRole(),
                (r) -> this.role = r,
                new UserRoleChanged(
                        id,
                        command.newRole(),
                        command.modifiedBy(),// Qui si passa l'utente che ha fatto la modifica
                        command.occurredAt(),
                        command.reason()
                )
        );

    }
    /*
    private void  changeStatusTo(UserStatus newStatus, DomainEvent event, UserStatusTransitionPolicy policy) throws InvalidUserStateException {
        //Objects.requireNonNull(newStatus, "Nuovo stato non può essere null");
        policy.validate(this.status, newStatus);
        mutateIfChanged(
                this.status,
                newStatus,
                (s) -> this.status = s,
                event
        );
    }
    */
    public void changeStatus(ChangeUserStatusCommand command, UserStatusTransitionPolicy policy) throws InvalidUserStateException{
        policy.validate(this.status, command.newStatus());

        DomainEvent event = switch (command.newStatus()) {
            case ACTIVE -> new UserActivated(id, command.occurredAt(), command.modifiedBy(), command.reason());
            case DISABLED -> new UserDeactivated(id, command.occurredAt(), command.modifiedBy(), command.reason());
            case BANNED -> new UserBanned(id, command.occurredAt(), command.modifiedBy(), command.reason());
            case DELETED -> new UserDeleted(id, command.occurredAt(), command.modifiedBy(), command.reason());

            case INACTIVE, SUSPENDED -> null;
        };

    }

    /*
    public void activate(UserStatusTransitionPolicy policy) throws InvalidUserStateException {

        changeStatusTo(UserStatus.ACTIVE, new UserActivated(id, Instant.now()), policy);

    }


    public void deactivate(UserStatusTransitionPolicy policy) throws InvalidUserStateException {
        if(this.status == UserStatus.BANNED){
            throw new InvalidUserStateException("Impossibile disattivare un utente bannato.");
        }
        changeStatusTo(UserStatus.DISABLED, new UserDeactivated(id, Instant.now()),policy);
    }


    public void ban(UserStatusTransitionPolicy policy) throws InvalidUserStateException {
        var rule = new CannotBanAdminRule(this);
        if(!rule.isSatisfied()) {
            throw new InvalidUserStateException(rule.message());
        }
        changeStatusTo(UserStatus.BANNED, new UserBanned(id, Instant.now()), policy);
    }
*/

    // ============================
    // Event handling
    // ============================

    private void registerEvent(DomainEvent event) {
        this.domainEvents.add(event);
        this.dirty = true;
    }

    public List<DomainEvent> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public boolean isDirty() {
        return dirty;
    }

    public void clearDomainEvents() {
        this.domainEvents.clear();
        this.dirty = false;
    }


    private <T> void mutateIfChanged(T currentValue, T newValue,Consumer<T> mutation ,DomainEvent event) {
        if (!Objects.equals(currentValue, newValue)) {
            mutation.accept(newValue); // Aggiorna il valore solo se è cambiato
            registerEvent(event);   // Registra l'evento solo se c'è stato un cambiamento
        }
    }
    // ============================
    // Getters
    // ============================

    public UserId getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public Nickname getNickname() {
        return nickname;
    }

    public Role getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }

    // ============================
    // Equals & HashCode
    // ============================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User other)) return false;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
