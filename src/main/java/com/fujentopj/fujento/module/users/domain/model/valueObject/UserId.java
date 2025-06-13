package com.fujentopj.fujento.module.users.domain.model.valueObject;

import java.util.Objects;
import java.util.UUID;

public final class UserId {
    private final UUID value;
    
    private UserId(UUID value){
        this.value = Objects.requireNonNull(value, "UserId value cannot be null");
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

    public UUID getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof UserId other && value.equals(other.value));
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
