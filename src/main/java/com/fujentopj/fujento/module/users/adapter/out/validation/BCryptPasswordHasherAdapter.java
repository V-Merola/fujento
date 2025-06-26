package com.fujentopj.fujento.module.users.adapter.out.validation;

import com.fujentopj.fujento.module.users.domain.model.valueObject.HashedPassword;
import com.fujentopj.fujento.module.users.port.out.PasswordHasherPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Component
public class BCryptPasswordHasherAdapter implements PasswordHasherPort {

    private final PasswordEncoder encoder;

    public BCryptPasswordHasherAdapter(PasswordEncoder encoder) {
        this.encoder = encoder;
    }


    @Override
    public HashedPassword hash(String rawPassword) {
        return HashedPassword.of(encoder.encode(rawPassword));
    }

    @Override
    public boolean verify(String rawPassword, HashedPassword hashedPassword) {
        return encoder.matches(rawPassword, hashedPassword.getValue());
    }
}
