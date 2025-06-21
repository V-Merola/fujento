package com.fujentopj.fujento.module.users.domain.service.policy;

import com.fujentopj.fujento.module.users.domain.model.aggregate.User;
import com.fujentopj.fujento.module.users.domain.model.enums.Role;
import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;

public interface UserPermissionPolicy {

    boolean canChangeEmail();

    boolean canChangeNickname(User target);

    boolean canChangePassword(User target);

    boolean canChangeRole(User target);

    boolean canChangeStatus(User target);

}
