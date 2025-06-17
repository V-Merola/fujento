package com.fujentopj.fujento.module.users.domain.model.aggregate;


import com.fujentopj.fujento.module.users.domain.event.*;
import com.fujentopj.fujento.module.users.domain.model.enums.Role;
import com.fujentopj.fujento.module.users.domain.model.enums.UserStatus;
import com.fujentopj.fujento.module.users.domain.model.exception.InvalidUserStateException;
import com.fujentopj.fujento.module.users.domain.model.valueObject.*;
import com.fujentopj.fujento.module.users.domain.service.UserValidator;
import com.fujentopj.fujento.module.users.domain.service.RoleAssignmentPolicy;
import com.fujentopj.fujento.module.users.domain.service.rule.CannotBanAdminRule;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


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

        /*
        if (!this.email.equals(newEmail)) {
            this.email = newEmail;
            //registerEvent(new UserEmailChanged(id.value(), newEmail.value()));
            registerEvent( new UserEmailChanged(
                    id,
                    newEmail,
                    Instant.now()
            ));
        }
         */
        registerEventIfChanged(!this.email.equals(newEmail),
                new UserEmailChanged(
                        id,
                        newEmail,
                        Instant.now()
                )
        );
    }

    public void changeNickname(Nickname newNickname, UserValidator validator) {
        validator.validateNickname(newNickname);
        /*
        if (!this.nickname.equals(newNickname)) {
            this.nickname = newNickname;
            //registerEvent(new UserNicknameChanged(id.value(), newNickname.value()));
            registerEvent(new UserNicknameChanged(
                    id,
                    newNickname,
                    Instant.now()
            ));
        }

         */
        registerEventIfChanged( !this.nickname.equals(newNickname),
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

        this.password = newPassword;
        //registerEvent(new UserPasswordChanged(id.value())); // opzionale: non includere hash
        registerEvent(new UserPasswordChanged(
                id,
                Instant.now()
        )
        );

    }

    private void  changeStatusTo(UserStatus newStatus, DomainEvent event){
        /*
        if(this.status != newStatus) {
            this.status = newStatus;
            registerEvent(event);
        }
         */
        registerEventIfChanged( !this.status.equals(newStatus),
                event
        );
    }



    //POTREBBE ESSERE SPLITTATO IN promoteTo e demoteTo PER CHIAREZZA SEMANTICA
    public void requestRoleChange(Role newRole, RoleAssignmentPolicy policy) throws InvalidUserStateException {
        if (!policy.canAssign(this.role, newRole)) {
            throw new InvalidUserStateException("Cambio ruolo non consentito: " + newRole);
        }

        if (!this.role.equals(newRole)) {
            this.role = newRole;
            registerEvent(new UserRoleChanged(
                    id,
                    newRole,
                    null,
                    Instant.now()
            ));
        }
    }

//    public void activate() {
//        if (this.status == UserStatus.BANNED) {
//            throw new InvalidUserStateException("Impossibile attivare un utente bannato.");
//        }
//
//        if (this.status != UserStatus.ACTIVE) {
//            this.status = UserStatus.ACTIVE;
//            //registerEvent(new UserActivated(id.value()));
//            registerEvent(new UserActivated(
//                    id,
//                    Instant.now()
//            ));
//        }
//    }
    // VERIFICARE SE IN CHANGE STATUS VIENE PASSATO CORRETTAMENTE L'EVENTO
    public void activate() throws InvalidUserStateException {
        if(this.status == UserStatus.BANNED){
            throw new InvalidUserStateException("Impossibile attivare un utente bannato.");
        }
        changeStatusTo(UserStatus.ACTIVE, new UserActivated(id, Instant.now()));

    }

//    public void deactivate() {
//        if (this.status == UserStatus.BANNED) {
//            throw new InvalidUserStateException("Impossibile disattivare un utente bannato.");
//        }
//
//        if (this.status != UserStatus.DISABLED) {
//            this.status = UserStatus.DISABLED;
//            //registerEvent(new UserDeactivated(id.value()));
//            registerEvent( new UserDeactivated(
//                    id,
//                    Instant.now()
//            ));
//        }
//    }

    public void deactivate() throws InvalidUserStateException {
        if(this.status == UserStatus.BANNED){
            throw new InvalidUserStateException("Impossibile disattivare un utente bannato.");
        }
        changeStatusTo(UserStatus.DISABLED, new UserDeactivated(id, Instant.now()));
    }

//    public void ban() {
//        if (this.status != UserStatus.BANNED) {
//            this.status = UserStatus.BANNED;
//           // registerEvent(new UserBanned(id.value()));
//            registerEvent(new UserBanned(
//                    id,
//                    Instant.now()
//            ));
//        }
//    }

    public void ban() throws InvalidUserStateException {
        var rule = new CannotBanAdminRule(this);
        if(!rule.isSatisfied()) {
            throw new InvalidUserStateException(rule.message());
        }

        changeStatusTo(UserStatus.BANNED, new UserBanned(id, Instant.now()));
    }


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

    private void registerEventIfChanged(boolean changed, DomainEvent event) {
        if (changed) {
            registerEvent(event);
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
