package com.fujentopj.fujento.module.users.application.command;

import com.fujentopj.fujento.module.users.domain.model.valueObject.Email;
import com.fujentopj.fujento.module.users.domain.model.valueObject.HashedPassword;
import com.fujentopj.fujento.module.users.domain.model.valueObject.Nickname;

public record RegisterUserCommand(
        Email email,
        String rawPassword,
        Nickname nickname
) {
    public RegisterUserCommand {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be blank");
        }
    } 
}
