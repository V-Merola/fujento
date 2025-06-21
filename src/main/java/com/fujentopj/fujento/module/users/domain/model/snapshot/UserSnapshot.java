package com.fujentopj.fujento.module.users.domain.model.snapshot;

import com.fujentopj.fujento.module.users.domain.model.enums.Role;
import com.fujentopj.fujento.module.users.domain.model.enums.UserStatus;
import com.fujentopj.fujento.module.users.domain.model.valueObject.Email;
import com.fujentopj.fujento.module.users.domain.model.valueObject.Nickname;
import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;

public record UserSnapshot(
        UserId id,
        Email email,
        Nickname nickname,
        Role role,
        UserStatus status
) {
}
