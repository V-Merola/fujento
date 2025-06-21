package com.fujentopj.fujento.module.users.domain.model.aggregate;


import com.fujentopj.fujento.module.users.domain.event.*;
import com.fujentopj.fujento.module.users.domain.model.enums.Role;
import com.fujentopj.fujento.module.users.domain.model.enums.UserStatus;
import com.fujentopj.fujento.module.users.domain.model.exception.InvalidUserStateException;
import com.fujentopj.fujento.module.users.domain.model.snapshot.UserSnapshot;
import com.fujentopj.fujento.module.users.domain.model.valueObject.*;
import com.fujentopj.fujento.module.users.domain.service.UserValidator;
import com.fujentopj.fujento.module.users.domain.service.policy.RoleAssignmentPolicy;
import com.fujentopj.fujento.module.users.domain.service.policy.UserPermissionPolicy;
import com.fujentopj.fujento.module.users.domain.service.policy.UserStatusTransitionPolicy;
import com.fujentopj.fujento.module.users.domain.service.rule.CannotBanAdminRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

//sarebbe opportuno eliminare per non avere dipendenze esterne?
import static com.fujentopj.fujento.module.users.domain.support.DomainEventLogger.logTransition;


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

    private static final Logger log = LoggerFactory.getLogger(User.class);

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

        //Registra l'evento di registrazione dell'utente
        // L'evento contiene lo snapshot dell'utente al momento della registrazione
        user.registerEvent(new UserRegistered(
                user.toSnapshot(),
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

    /**
     * Costruttore protetto per ORM (es. Hibernate).
     * Non dovrebbe essere usato direttamente, ma solo da framework di persistenza.
     *
     * @deprecated Per favore usa il factory method {@link #register(UserId, Email, HashedPassword, Nickname, Role, UserValidator)}
     */
    @Deprecated(forRemoval = false)
    protected User() {
        this.id = null; // Per ORM
    }

    // ============================
    // Comportamenti di dominio
    // ============================

    /*
    public void changeEmail(ChangeUserEmailCommand command, UserValidator validator, UserPermissionPolicy permPolicy) {
        // Verifica aggregate identity

        //Fixa il problema di null pointer exception
        if (this.id == null) {
            throw new IllegalStateException("User ID non può essere null");
        }
        if(!this.id.equals(command.userId())){
            throw new IllegalArgumentException("Command userId diverso da aggregate");
        }

        if(!permPolicy.canChangeEmail(this) ){
            throw new InvalidUserStateException("Permessi insufficienti per cambiare il nickname.");
        }

        validator.validateEmail(command.newEmail());

        mutateIfChanged(
                this.email,
                command.newEmail(),
                (e) -> this.email = e,
                UserEmailChanged.of(
                        this.toSnapshot(),
                        command.modifiedBy(),
                        command.reason().orElse(null)
                )
        );

    }
*/

    public void changeEmail(Email newEmail, UserId modifiedBy, Optional<String> reason,
                            UserValidator validator, UserPermissionPolicy permissionPolicy
    ) {
        if (!permissionPolicy.canChangeEmail(this)) {
            throw new InvalidUserStateException("Permessi insufficienti per cambiare l'email.");
        }
        validator.validateEmail(newEmail);

        mutateIfChanged(
                this.email,
                newEmail,
                (e) -> this.email = e,
                UserEmailChanged.of(
                        this.toSnapshot(),
                        modifiedBy,
                        reason.orElse(null)
                )
        );
    }


    public void changeNickname(Nickname newNickname, UserValidator validator) {
        validator.validateNickname(newNickname);

        mutateIfChanged(
                this.nickname,
                newNickname,
                (n) -> this.nickname = n,
                UserNicknameChanged.of(
                        this.toSnapshot(),
                        this.id, // Qui si passa l'utente che ha fatto la modifica, potrebbe essere null se non è stato specificato
                        null // Qui si può passare un motivo opzionale per il cambio nickname
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

    public void changeRole(Role newRole, UserId modifiedBy, Optional<String> reason,
                           UserValidator validator, RoleAssignmentPolicy rolePolicy,
                           UserPermissionPolicy permissionPolicy) throws InvalidUserStateException {
        // Permessi di chi chiede la modifica
        if (!permissionPolicy.canChangeRole(this)) {
            throw new InvalidUserStateException("Permessi insufficienti per cambiare il ruolo.");
        }
        // Validazioni di dominio (se serve validator per regole aggiuntive)
        // Ad es si potrebbe validare che non ci siano conflitti di stato attuale e nuovo ruolo:
        validator.validateRoleAssignment(this.role, newRole);

        if (Objects.equals(this.role, newRole)) {
            return;
        }

        // applica la transizione di ruolo tramite policy
        if (!rolePolicy.canAssign(this.role, newRole)) {
            throw new InvalidUserStateException("Cambio ruolo non consentito: " + newRole);
        }
        // Cattura snapshot pre-modifica
        UserSnapshot oldSnapshot = this.toSnapshot();
        this.role = newRole;

        // Registra l'evento di cambio ruolo
        mutateIfChanged(
                this.role,
                newRole,
                (r) -> this.role = r,
                UserRoleChanged.of(
                        oldSnapshot,
                        modifiedBy,
                        reason.orElse(null)
                )
        );
    }

    public void changeStatus(UserStatus newStatus, UserId modifiedBy, Optional<String> reason,
                             UserStatusTransitionPolicy transitionPolicy, UserPermissionPolicy permissionPolicy) throws InvalidUserStateException {

        // Controlla i permessi
        if (!permissionPolicy.canChangeRole(this)) {
            throw new InvalidUserStateException("Permessi insufficienti per cambiare lo stato dell'utente.");
        }
        // Controlla la transizione di stato
        transitionPolicy.validate(this.status, newStatus);

        if (this.status == newStatus) {
            return; // Nessun cambiamento, esci subito
        }

            //ALTRE REGOLE........

            //Cattura snapshot pre-modifica
            UserSnapshot oldSnapshot = this.toSnapshot();

        DomainEvent event = switch (newStatus) {

            case ACTIVE -> UserActivated.of(id, modifiedBy, reason.orElse(null));

            case DISABLED -> UserDeactivated.of(oldSnapshot, modifiedBy, reason.orElse(null));

            case BANNED -> {
                var adminRule = new CannotBanAdminRule(this);
                if (!adminRule.isSatisfied()) throw new InvalidUserStateException(adminRule.message());
                yield UserBanned.of(oldSnapshot, modifiedBy, reason.orElse(null));
            }
            case DELETED -> UserDeleted.of(oldSnapshot, modifiedBy, reason.orElse(null));

            case ARCHIVED -> UserArchived.of(oldSnapshot, modifiedBy, reason.orElse(null));
        };
        // Aggiorna lo stato
        this.status = newStatus;
            // Registra evento
        mutateIfChanged(
                this.status,
                newStatus,
                (s) -> this.status = s,
                event
        );

        // Log di transizione
        logTransition(log, event, "UserStatusChange");

        }




    // ============================
    // Snapshot
    // ============================
    /**Questo metodo crea uno snapshot dell'utente, utile per la persistenza o la serializzazione.*/

    public UserSnapshot toSnapshot() {
        return new UserSnapshot(
                this.id,
                this.email,
                this.nickname,
                this.role,
                this.status
        );
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
