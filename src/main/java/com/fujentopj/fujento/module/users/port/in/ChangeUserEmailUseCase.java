package com.fujentopj.fujento.module.users.port.in;

import com.fujentopj.fujento.module.users.application.command.ChangeUserEmailCommand;

public interface ChangeUserEmailUseCase {
    void handle(ChangeUserEmailCommand command);
}
