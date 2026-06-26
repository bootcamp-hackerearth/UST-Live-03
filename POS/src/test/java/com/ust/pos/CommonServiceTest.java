package com.ust.pos;

import com.ust.pos.commonservice.CommonService;
import com.ust.pos.model.CommonFields;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CommonServiceTest {

    static class TestService extends CommonService {}

    static class TestEntity extends CommonFields {}

    private final TestService service = new TestService();

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testSetAuditFields_NewEntity() {
        TestEntity entity = new TestEntity();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("testUser", null, new ArrayList<>())
        );

        service.setAuditFields(entity, true);

        assertEquals("testUser", entity.getCreatedBy());
        assertNotNull(entity.getCreatedOn());

        assertNull(entity.getModifiedBy());
        assertNull(entity.getModifiedOn());
    }

    @Test
    void testSetAuditFields_Update() {
        TestEntity entity = new TestEntity();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("updatedUser", null, new ArrayList<>())
        );

        service.setAuditFields(entity, false);

        assertEquals("updatedUser", entity.getModifiedBy());
        assertNotNull(entity.getModifiedOn());
    }

    @Test
    void testSetAuditFields_NoAuthentication() {
        TestEntity entity = new TestEntity();

        SecurityContextHolder.clearContext(); // no auth

        service.setAuditFields(entity, true);

        assertEquals("system", entity.getCreatedBy());
        assertNotNull(entity.getCreatedOn());
    }

    @Test
    void testSoftDelete() {
        TestEntity entity = new TestEntity();
        entity.setDeleted(false);
        entity.setStatus(true);

        service.softDelete(entity);

        assertTrue(entity.isDeleted());
        assertFalse(entity.isStatus());
    }
}
