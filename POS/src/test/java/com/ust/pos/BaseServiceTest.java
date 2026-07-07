package com.ust.pos;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.models.CommonFields;
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
    void setCreatedDetailsTest() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "testUser",
                        "password",
                        Collections.emptyList())
        );

        TestEntity authenticatedEntity = new TestEntity();
        service.invokeSetCreatedDetails(authenticatedEntity);
        assertEquals("testUser", authenticatedEntity.getCreatedBy());
        assertEquals("testUser", authenticatedEntity.getModifiedBy());
        assertNotNull(authenticatedEntity.getCreatedOn());
        assertNotNull(authenticatedEntity.getModifiedOn());
        SecurityContextHolder.clearContext();
        TestEntity systemEntity = new TestEntity();
        service.invokeSetCreatedDetails(systemEntity);
        assertEquals("SYSTEM", systemEntity.getCreatedBy());
        assertEquals("SYSTEM", systemEntity.getModifiedBy());
        assertNotNull(systemEntity.getCreatedOn());
        assertNotNull(systemEntity.getModifiedOn());
        assertDoesNotThrow(() -> service.invokeSetCreatedDetails(null));
    }

    @Test
    void setModifiedDetailsTest() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("testUser", "password", Collections.emptyList()));
        TestEntity authenticatedEntity = new TestEntity();
        service.invokeSetModifiedDetails(authenticatedEntity);
        assertEquals("testUser", authenticatedEntity.getModifiedBy());
        assertNotNull(authenticatedEntity.getModifiedOn());
        SecurityContextHolder.clearContext();
        TestEntity systemEntity = new TestEntity();
        service.invokeSetModifiedDetails(systemEntity);
        assertEquals("SYSTEM", systemEntity.getModifiedBy());
        assertNotNull(systemEntity.getModifiedOn());
        assertDoesNotThrow(() -> service.invokeSetModifiedDetails(null));
    }

    @Test
    void softDeleteTest() {
        TestEntity entity = new TestEntity();
        entity.setDeleted(false);
        service.invokeSoftDelete(entity);
        assertTrue(entity.getDeleted());
    }

    @Test
    void nullEntityMethodsDoNotThrowTest() {
        assertAll(
                () -> assertDoesNotThrow(() -> service.invokeSetCreatedDetails(null)),
                () -> assertDoesNotThrow(() -> service.invokeSetModifiedDetails(null))
        );
    }
}