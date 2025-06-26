package com.fujentopj.fujento.module.users.port.in;

import com.fujentopj.fujento.module.users.application.command.RegisterUserCommand;
import com.fujentopj.fujento.module.users.domain.model.aggregate.User;

public interface RegisterUserUseCase {
    User register(RegisterUserCommand command);
}
