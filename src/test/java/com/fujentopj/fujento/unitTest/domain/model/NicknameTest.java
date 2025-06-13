package com.fujentopj.fujento.unitTest.domain.model;

import com.fujentopj.fujento.module.users.domain.model.valueObject.Nickname;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NicknameTest {

    @Test
    void shouldCreateValidNickname() {
        Nickname nickname = Nickname.of("Sportivo_123");
        assertEquals("Sportivo_123", nickname.getValue());
    }

    @Test
    void shouldRejectInvalidNickname() {
        assertThrows(IllegalArgumentException.class, () -> Nickname.of(""));
        assertThrows(IllegalArgumentException.class, () -> Nickname.of("ab"));
        assertThrows(IllegalArgumentException.class, () -> Nickname.of("nickname_troppo_lungo_oltre_il_limite") );
        assertThrows(IllegalArgumentException.class, () -> Nickname.of("nick name"));
        assertThrows(IllegalArgumentException.class, () -> Nickname.of("nick!@"));
    }
}
