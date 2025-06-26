package com.fujentopj.fujento.module.users.domain.model.valueObject;

import java.util.Objects;

public final class HashedPassword {
    private final String value;

    private HashedPassword(String value) {
        this.value = value;
    }

    /**
     *  Factory method
     */

    public static HashedPassword of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Hashed password cannot be null or blank");
        }
        String trimmedValue = value.trim();
        if (!trimmedValue.matches("^\\$2[aby]\\$.{56}$")) {
            throw new IllegalArgumentException("Not a valid bcrypt hash");
        }
//        if (trimmedValue.length() <60) {
//            throw new IllegalArgumentException("Hashed password must be at least 60 characters long");
//        }
        return new HashedPassword(trimmedValue);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o){
        return this == o || (o instanceof HashedPassword other && value.equals(other.value));
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "******************"; // Masked for security
    }
}
