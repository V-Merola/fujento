package com.fujentopj.fujento.unitTest.domain.model;

import com.fujentopj.fujento.module.users.domain.model.valueObject.HashedPassword;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HashedPasswordTest {
    @Test
    void shouldAcceptValidHash() {
        HashedPassword hp = HashedPassword.of("$2a$10$abcdefghijklmnopqrstuv1234567890ABCDEFGHijklmnfdwatpd");
        assertNotNull(hp);
    }

    @Test
    void shouldRejectEmptyOrShort() {
        assertThrows(IllegalArgumentException.class, () -> HashedPassword.of(""));
        assertThrows(IllegalArgumentException.class, () -> HashedPassword.of("short"));
    }
}
