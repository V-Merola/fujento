package com.fujentopj.fujento.unitTest.domain.model;

import com.fujentopj.fujento.module.users.domain.model.enums.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RoleTest {
    @Test
    void shouldConvertRoleToStringAndBack() {
        Role role = Role.MANAGER;
        String name = role.name();

        // Convert Role to String
        assertEquals("MANAGER", name);
        assertEquals(Role.MANAGER, Role.valueOf(name));

        System.out.println("Role: " + role);
    }

}
