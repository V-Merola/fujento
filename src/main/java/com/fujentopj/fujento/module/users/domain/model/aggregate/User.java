package com.fujentopj.fujento.module.users.domain.model.aggregate;


import com.fujentopj.fujento.module.users.domain.event.*;
import com.fujentopj.fujento.module.users.domain.model.enums.Role;
import com.fujentopj.fujento.module.users.domain.model.enums.UserStatus;
import com.fujentopj.fujento.module.users.domain.model.exception.InvalidUserStateException;
import com.fujentopj.fujento.module.users.domain.model.snapshot.UserSnapshot;
import com.fujentopj.fujento.module.users.domain.model.valueObject.*;
import com.fujentopj.fujento.module.users.domain.service.UserValidator;
import com.fujentopj.fujento.module.users.domain.service.policy.RoleAssignmentPolicy;
import com.fujentopj.fujento.module.users.domain.service.policy.UserStatusTransitionPolicy;
import com.fujentopj.fujento.module.users.domain.service.rule.CannotBanAdminRule;



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


    // ============================
    // Factory Method
    // ============================
    public static User register(
            UserId id,
            Email email,
            HashedPassword password,
            Nickname nickname,
            Role role
            //Spostare la logica di validazione in un Application Service?
           // UserValidator validator
    ) {
        //La validazione di unicità (email duplicata)
        // deve essere effettuata dall’Application Service / Domain Service
        // che conosce il repository, non dal dominio puro.
        // Il UserValidator probabilmente deve delegare a un’interfaccia (port out) UniqueEmailChecker,
        // chiamato dall’Application Service, non dal dominio.
       // validator.validateEmail(email);
        //validator.validateNickname(nickname);
        //validator.validateRoleAssignment(null, role);

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
     * @deprecated Per favore usa il factory method {@link#register(UserId, Email, HashedPassword, Nickname, Role, UserValidator)}
     */
    @Deprecated(forRemoval = false)
    protected User() {
        this.id = null; // Per ORM
    }

    // ============================
    // Comportamenti di dominio
    // ============================

    public void changeEmail(Email newEmail, UserId modifiedBy, Optional<String> reason) {
        //spostato in ChangeUserEmailService
//        if (!permissionPolicy.canChangeEmail(this)) {
//            throw new InvalidUserStateException("Permessi insufficienti per cambiare l'email.");
//        }
        //Spostato in ChangeEmailService: chiama DB
       // validator.validateEmail(newEmail);

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
                            RoleAssignmentPolicy rolePolicy
                           ) throws InvalidUserStateException {

        // non nel dominio puro, ma qui lo mettiamo per completezza.
        if (newRole == null) {
            throw new InvalidUserStateException("Il nuovo ruolo non può essere null.");
        }
        //Spostato nel layer application: service ChangeUserRoleService
//        if (!permissionPolicy.canChangeRole(this)) {
//            throw new InvalidUserStateException("Permessi insufficienti per cambiare il ruolo.");
//        }
        // Validazioni di dominio (se serve validator per regole aggiuntive)
        // Ad es si potrebbe validare che non ci siano conflitti di stato attuale e nuovo ruolo:
        //spostato nel layer application: service ChangeUserRoleService
        //validator.validateRoleAssignment(this.role, newRole);

        if (Objects.equals(this.role, newRole)) {
            return;
        }

        // applica la transizione di ruolo tramite policy
        if (!rolePolicy.canAssign(this.role, newRole)) {
            throw new InvalidUserStateException("Cambio ruolo non consentito: " + newRole);
        }
        // Cattura snapshot pre-modifica
        UserSnapshot oldSnapshot = this.toSnapshot();


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
                             UserStatusTransitionPolicy transitionPolicy) throws InvalidUserStateException {

        // Controlla i permessi. Stessa cosa del metodo changeRole.
        // Nota: il controllo dei permessi è fatto nel layer di Application Service, classe : ChangeUserStatusService
        // non nel dominio puro, ma qui lo mettiamo per completezza.
//        if (!permissionPolicy.canChangeStatus(this)) {
//            throw new InvalidUserStateException("Permessi insufficienti per cambiare lo stato dell'utente.");
//        }
        // Controlla la transizione di stato
        if( newStatus == null) {
            throw new InvalidUserStateException("Nuovo stato non può essere null.");
        }

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
            // Registra evento
        mutateIfChanged(
                this.status,
                newStatus,
                (s) -> this.status = s,
                event
        );

        }

    //AGGIUNGERE METODI PER USER DELETED / ARCHIVED
    /**
     * Metodo per archiviare l'utente.
     * Questo metodo può essere usato per archiviare un utente senza eliminarlo fisicamente dal database.
     * L'utente viene marcato come ARCHIVED e viene registrato un evento di archiviazione.
     */
    public void archive(UserId modifiedBy, Optional<String> reason) {
        if (this.status == UserStatus.DELETED || this.status == UserStatus.ARCHIVED) {
            throw new InvalidUserStateException("Utente già archiviato o eliminato.");
        }

        // Cattura snapshot pre-modifica
        UserSnapshot oldSnapshot = this.toSnapshot();

        // Cambia lo stato a ARCHIVED
        this.status = UserStatus.ARCHIVED;

        //implementa la logica per archiviare l'utente?



        // Registra l'evento di archiviazione
        mutateIfChanged(
                this.status,
                UserStatus.ARCHIVED,
                (s) -> this.status = s,
                UserArchived.of(oldSnapshot, modifiedBy, reason.orElse(null))
        );
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
