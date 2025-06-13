package com.fujentopj.fujento.unitTest.domain.model;

import com.fujentopj.fujento.module.users.domain.model.valueObject.UserId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserIdTest {
    @Test
    void shouldCreateDifferentIds(){
        UserId id1 = UserId.create();
        UserId id2 = UserId.create();

        assertNotEquals(id1, id2, "IDs should be different");
    }

    @Test
    void shouldCreateSameIdFromString() {
        UserId id1 = UserId.create();
        UserId id2 = UserId.fromString(id1.toString());

        assertEquals(id1, id2, "IDs created from the same string should be equal");
    }

    @Test
    void shouldCreateSameIdFromSameUUID() {
        UUID uuid = UUID.randomUUID();
        UserId id1 = UserId.fromUUID(uuid);
        UserId id2 = UserId.fromUUID(uuid);
        assertEquals(id1, id2, "IDs created from the same UUID should be equal");
    }

    @Test
    void shouldConvertToStringCorrectly(){
        UUID uuid = UUID.randomUUID();
        UserId  id = UserId.fromUUID(uuid);

        assertEquals(uuid.toString(), id.toString());
    }
}
