package com.fujentopj.fujento.module.users.port.out;

import com.fujentopj.fujento.module.users.domain.model.valueObject.HashedPassword;

public interface PasswordHasherPort {
    HashedPassword hash(String rawPassword);
    boolean verify(String rawPassword, HashedPassword hashedPassword);

}
