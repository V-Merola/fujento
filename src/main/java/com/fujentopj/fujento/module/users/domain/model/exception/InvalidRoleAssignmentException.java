package com.fujentopj.fujento.module.users.domain.model.exception;

public class InvalidRoleAssignmentException extends RuntimeException {
    public InvalidRoleAssignmentException(String message) {
        super(message);
    }
}
