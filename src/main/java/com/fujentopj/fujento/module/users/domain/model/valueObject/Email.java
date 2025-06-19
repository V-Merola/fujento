package com.fujentopj.fujento.module.users.domain.model.valueObject;

import java.util.Objects;
import java.util.regex.Pattern;


//POSSIBILE MIGRAZIONE A RECORD: EMAIL DOVREBBE ESSERE IMMUTABILE E NON CAMBIARE DOPO LA CREAZIONE?

public final class Email {
    private static Pattern EMAIL_REGEX = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    private final String value;

    private Email (String value){
        this.value = value;
    }

    public static Email of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Email obbligatoria");
        }
        String trimmedValue = value.trim().toLowerCase();
        if (!EMAIL_REGEX.matcher(trimmedValue).matches()) {
            throw new IllegalArgumentException("Formato email non valido: " + trimmedValue);
        }
        return new Email(trimmedValue);
    }

    public String getValue() {
        return value;
    }
    @Override
    public boolean equals(Object o){
        return this == o || (o instanceof Email other && value.equals(other.value));
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
