package com.fujentopj.fujento.unitTest.domain.model;

import com.fujentopj.fujento.module.users.domain.model.valueObject.Email;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EmailTest {
    @Test
    void shouldCreateValidEmail() {
        Email email = Email.of("Test.User@Example.COM");
        assertEquals("test.user@example.com", email.getValue());
    }

    @Test
    void shouldRejectInvalidEmail() {
        assertThrows(IllegalArgumentException.class, () -> Email.of("notanemail"));
        assertThrows(IllegalArgumentException.class, () -> Email.of("a@b"));
        assertThrows(IllegalArgumentException.class, () -> Email.of(""));
    }
}
