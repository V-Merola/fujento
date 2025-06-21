package com.fujentopj.fujento.module.users.domain.model.exception;

public class InvalidUserStateException extends RuntimeException {
    public InvalidUserStateException(String message) {
        super(message);
    }
}
