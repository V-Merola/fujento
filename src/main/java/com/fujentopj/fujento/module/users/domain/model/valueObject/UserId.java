package com.fujentopj.fujento.module.users.domain.model.valueObject;

import com.fujentopj.fujento.module.users.domain.model.common.Identifier;

import java.util.Objects;
import java.util.UUID;

public record UserId(UUID value) implements Identifier {
    //private final UUID value;
    
    //private UserId(UUID value){
    //    this.value = Objects.requireNonNull(value, "UserId value cannot be null");
    //}

    public UserId {
        Objects.requireNonNull(value, "UserId non può essere null");
    }

    /*
     * Crea un nuovo ID utente con un UUID random.
     */

    public static UserId create() {
        return new UserId(UUID.randomUUID());
    }

    /*
    * Ricerca un UserId da una stringa UUID.
    */
    public static UserId fromString(String stringId) {
        return new UserId(UUID.fromString(stringId));
    }

    /*
     * Ricrea un UserId da un UUID.
     */
    public static UserId fromUUID(UUID uuid) {
        return new UserId(uuid);
    }

    public static UserId of(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("UserId non può essere null");
        }
        return new UserId(id);
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof UserId(UUID value1) && value.equals(value1));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
