package com.fujentopj.fujento.unitTest.fixture;


import com.fujentopj.fujento.module.users.domain.model.aggregate.User;
import com.fujentopj.fujento.module.users.domain.model.enums.Role;
import com.fujentopj.fujento.module.users.domain.model.enums.UserStatus;
import com.fujentopj.fujento.module.users.domain.model.valueObject.Email;
import com.fujentopj.fujento.module.users.domain.model.valueObject.HashedPassword;
import com.fujentopj.fujento.module.users.domain.model.valueObject.Nickname;
import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;
/*
public class UserTestBuilder {

    private UserId id = UserId.generate();
    private Email email = new Email("test@example.com");
    private HashedPassword password = HashedPassword.fromHashed("hashedpwd");
    private Nickname nickname = new Nickname("testuser");
    private Role role = Role.PLAYER;
    private UserStatus status = UserStatus.ACTIVE;

    public static UserTestBuilder anActiveUser() {
        return new UserTestBuilder();
    }

    public UserTestBuilder withEmail(String email) {
        this.email = new Email(email);
        return this;
    }

    public UserTestBuilder withRole(Role role) {
        this.role = role;
        return this;
    }

    public User build() {
        return new User(id, email, password, nickname, role, status);
    }
}

 */