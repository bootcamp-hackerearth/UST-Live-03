package com.ust.pos;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.modell.CommonFields;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class BaseServiceTest {
    private final TestBaseService service = new TestBaseService();
    static class TestBaseService extends BaseService {
        void invokeSetCreatedDetails(CommonFields entity) {
            setCreatedDetails(entity);
        }
        void invokeSetModifiedDetails(CommonFields entity) {
            setModifiedDetails(entity);
        }
        void invokeSoftDelete(CommonFields entity) {
            softDelete(entity);
        }
    }
    static class TestEntity extends CommonFields {
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void setCreatedDetailsWithAuthenticatedUserTest() {

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "testUser",
                        "password",
                        Collections.emptyList()
                )
        );

        TestEntity entity = new TestEntity();
        service.invokeSetCreatedDetails(entity);
        assertEquals("testUser", entity.getCreatedBy());
        assertEquals("testUser", entity.getModifiedBy());
        assertNotNull(entity.getCreatedOn());
        assertNotNull(entity.getModifiedOn());
    }

    @Test
    void setCreatedDetailsWithoutAuthenticationTest() {
        TestEntity entity = new TestEntity();
        service.invokeSetCreatedDetails(entity);
        assertEquals("SYSTEM", entity.getCreatedBy());
        assertEquals("SYSTEM", entity.getModifiedBy());
        assertNotNull(entity.getCreatedOn());
        assertNotNull(entity.getModifiedOn());
    }

    @Test
    void setCreatedDetailsWithNullEntityTest() {
        assertDoesNotThrow(() -> service.invokeSetCreatedDetails(null));
    }

    @Test
    void setModifiedDetailsWithAuthenticatedUserTest() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "testUser",
                        "password",
                        Collections.emptyList()
                )
        );

        TestEntity entity = new TestEntity();
        service.invokeSetModifiedDetails(entity);
        assertEquals("testUser", entity.getModifiedBy());
        assertNotNull(entity.getModifiedOn());
    }

    @Test
    void setModifiedDetailsWithoutAuthenticationTest() {
        TestEntity entity = new TestEntity();
        service.invokeSetModifiedDetails(entity);
        assertEquals("SYSTEM", entity.getModifiedBy());
        assertNotNull(entity.getModifiedOn());
    }

    @Test
    void setModifiedDetailsWithNullEntityTest() {
        assertDoesNotThrow(() -> service.invokeSetModifiedDetails(null));
    }

    @Test
    void softDeleteTest() {
        TestEntity entity = new TestEntity();
        entity.setDeleted(false);
        service.invokeSoftDelete(entity);
        assertTrue(entity.getDeleted());
    }
}