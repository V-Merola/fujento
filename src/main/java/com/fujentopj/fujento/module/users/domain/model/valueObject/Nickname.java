package com.fujentopj.fujento.module.users.domain.model.valueObject;

import java.io.Serializable;
import java.util.regex.Pattern;

public final class Nickname implements Serializable {
    private static final Pattern VALID_PATTERN = Pattern.compile("^[A-Za-z0-9_]{3,20}$");
    private final String value;

    private Nickname(String value){
        this.value = value;
    }

    /**
     * Factory Method
     */
    public static Nickname of(String raw){
        if(raw == null || raw.isBlank()){
            throw new IllegalArgumentException("Nickname required");
        }
        String trimmed = raw.trim();

        if(!VALID_PATTERN.matcher(trimmed).matches()){
            throw new IllegalArgumentException("Invalid nickname: must contain only letters, numbers or underscores (3–20 characters)");
        }

        return new Nickname(trimmed);
    }

    public String getValue(){
        return value;
    }
    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Nickname other && value.equalsIgnoreCase(other.value));
    }

    @Override
    public int hashCode() {
        return value.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}