package com.fujentopj.fujento.module.users.port.out;

import com.fujentopj.fujento.module.users.domain.model.aggregate.User;
import com.fujentopj.fujento.module.users.domain.model.valueObject.Email;
import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(UserId userId);
    void save(User user);
    boolean existsByEmail(Email email);

    //Altri metodi domain-oriented non jpa oriented
}
