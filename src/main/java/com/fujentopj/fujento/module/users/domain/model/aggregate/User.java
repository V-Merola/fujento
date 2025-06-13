package com.fujentopj.fujento.module.users.domain.model.aggregate;


import com.fujentopj.fujento.module.users.domain.model.enums.Role;
import com.fujentopj.fujento.module.users.domain.model.enums.UserStatus;
import com.fujentopj.fujento.module.users.domain.model.exception.InvalidUserStateException;
import com.fujentopj.fujento.module.users.domain.model.valueObject.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
    private final UserId id; //Value Object immutabile
    private Email email; 	// Validato da UserValidator
    private HashedPassword password; //hashed, validato
    private Nickname nickname;
    private String bio; //opzionale
    private String imageUrl; //opzionale
    private Set<Role> roles = new HashSet<>();
    private UserStatus status;
    private Set<SportPreference> sportPreferences = new HashSet<>();

    /**
     * Costruttore protetto, usato solo internamente o dai factory method
     */
    protected User(UserId id, Email email, HashedPassword password, Nickname nickname){
        this.id = Objects.requireNonNull(id);
        this.email = Objects.requireNonNull(email, "Email cannot be null or empty");
        this.password = Objects.requireNonNull(password, "Hashed Password obbligatoria");
        this.nickname = Objects.requireNonNull(nickname, "Nickname obbligatorio");
        this.status = UserStatus.ACTIVE;
        this.roles.add(Role.PLAYER); // ruolo di default
    }

    /**
     * Factory method per creare un nuovo utente a partire da dati minimi
     */
    public static User register(Email email, HashedPassword hashedPassword, Nickname nickname) {
        return new User(UserId.create(), email, hashedPassword, nickname);
    }

    private String requireNonEmpty(String value, String message) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(message);
        return value.trim();
    }

    /**
     * Altri metodi di comportamento per gestire l'utente
     */
    public void updateProfile(Nickname newNickname, String bio, String imageUrl, Set<SportPreference> sportPreferences){
        if(newNickname != null) {
            changeNickname(newNickname);
        }
        this.bio = (bio != null && !bio.isBlank()) ? bio.trim() : null;

        this.imageUrl = (imageUrl != null && !imageUrl.isBlank()) ? imageUrl.trim() : null;

        if(sportPreferences != null){
            this.sportPreferences = Set.copyOf(sportPreferences);
        }
    }

    public void assignRole(Role role) {
        if (role == null) throw new IllegalArgumentException("Role cannot be null");
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        if (role != null && !role.equals(Role.PLAYER)) { // non rimuovere il ruolo PLAYER
            this.roles.remove(role);
        }
    }

    public void changeStatus(UserStatus newStatus) {
        if (newStatus == null) throw new IllegalArgumentException("UserStatus cannot be null");
        this.status = newStatus;
    }

    /**
     * Cambia la password utente.
     * La password fornita deve essere già hashata.
     */
    public void changePassword(HashedPassword newHashedPassword) throws InvalidUserStateException {
        if (this.status != UserStatus.ACTIVE) {
            throw new InvalidUserStateException("Solo utenti attivi possono cambiare password");
        }
        this.password = Objects.requireNonNull(newHashedPassword, "Hashed Password required");
    }

    /**
     * Cambia l'email associata all'utente.
     * Si assume che l’unicità e validità siano già verificate a monte.
     */
    public void changeEmail(Email newEmail) {
        this.email = Objects.requireNonNull(newEmail, "Email cannot be null or empty");
    }

    /**
     * Cambia il nickname dell'utente.
     * Si assume che l'unicità e validità siano già verificate a monte.
     */
    public void changeNickname(Nickname newNickname) {
        this.nickname = Objects.requireNonNull(newNickname, "Nickname cannot be null");
    }


    // === GETTER ===

    public UserId getId() {
        return id;
    }

    public Email getEmail() {
        return email;
    }

    public Nickname getNickname() {
        return nickname;
    }

    public HashedPassword getPassword() {
        return password;
    }

    public String getBio() {
        return bio;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public UserStatus getStatus() {
        return status;
    }

    public Set<SportPreference> getSportPreferences() {
        return Collections.unmodifiableSet(sportPreferences);
    }
}
