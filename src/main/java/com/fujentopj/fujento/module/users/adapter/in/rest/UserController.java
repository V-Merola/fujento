package com.fujentopj.fujento.module.users.adapter.in.rest;


import com.fujentopj.fujento.module.users.adapter.in.rest.dto.request.RegisterUserRequestDTO;
import com.fujentopj.fujento.module.users.application.command.RegisterUserCommand;
import com.fujentopj.fujento.module.users.application.service.RegisterUserService;
import com.fujentopj.fujento.module.users.domain.event.DomainEvent;
import com.fujentopj.fujento.module.users.domain.model.aggregate.User;
import com.fujentopj.fujento.module.users.domain.model.valueObject.Email;
import com.fujentopj.fujento.module.users.domain.model.valueObject.Nickname;
import com.fujentopj.fujento.module.users.port.in.RegisterUserUseCase;
import com.fujentopj.fujento.module.users.port.out.DomainEventPublisher;
import jakarta.validation.Valid;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final RegisterUserUseCase registerUserUseCase;
    private final DomainEventPublisher eventPublisher;

    public UserController(RegisterUserUseCase registerUserUseCase, DomainEventPublisher eventPublisher) {
        this.registerUserUseCase = registerUserUseCase;
        this.eventPublisher = eventPublisher;
    }
    @PostMapping("/register")
    public ResponseEntity<?> register (@Valid @RequestBody RegisterUserRequestDTO dto){
        RegisterUserCommand command = new RegisterUserCommand(
                Email.of(dto.getEmail()),
                dto.getPassword(),
                Nickname.of(dto.getNickname())
        );
        registerUserUseCase.register(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


}
