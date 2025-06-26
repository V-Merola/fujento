package com.fujentopj.fujento.module.users.adapter.in.rest;


import com.fujentopj.fujento.module.users.adapter.in.rest.dto.request.RegisterUserRequestDTO;
import com.fujentopj.fujento.module.users.application.command.RegisterUserCommand;
import com.fujentopj.fujento.module.users.domain.model.valueObject.Email;
import com.fujentopj.fujento.module.users.domain.model.valueObject.Nickname;
import com.fujentopj.fujento.module.users.port.in.RegisterUserUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final RegisterUserUseCase registerUserUseCase;

    public UserController(RegisterUserUseCase registerUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
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
