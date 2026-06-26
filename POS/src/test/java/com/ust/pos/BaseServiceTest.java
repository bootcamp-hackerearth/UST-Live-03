package com.ust.pos;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.model.CommonFields;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaseServiceTest {

    private BaseService baseService;
    private ConcreteCommonFields entity;
    private MockedStatic<SecurityContextHolder> mockedSecurityContextHolder;
    private SecurityContext mockSecurityContext;
    private Authentication mockAuthentication;

    @BeforeEach
    void setUp() {
        baseService = new BaseService();
        entity = new ConcreteCommonFields();
        mockedSecurityContextHolder = Mockito.mockStatic(SecurityContextHolder.class);
        mockSecurityContext = mock(SecurityContext.class);
        mockAuthentication = mock(Authentication.class);
        mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(mockSecurityContext);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityContextHolder.close();
    }

    @Test
    void testSetCreatedDetails_WithLoggedInUser() {
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.getName()).thenReturn("john_doe");
        baseService.setCreatedDetails(entity);
        assertEquals("john_doe", entity.getCreatedBy());
        assertNotNull(entity.getCreatedOn());
        assertEquals("john_doe", entity.getModifiedBy());
        assertNotNull(entity.getModifiedOn());
        assertFalse(entity.getCreatedOn().isAfter(LocalDateTime.now()));
    }

    @Test
    void testSetCreatedDetails_WithNoAuthentication_FallbackToSystem() {
        when(mockSecurityContext.getAuthentication()).thenReturn(null);
        baseService.setCreatedDetails(entity);
        assertEquals("SYSTEM", entity.getCreatedBy());
        assertNotNull(entity.getCreatedOn());
        assertEquals("SYSTEM", entity.getModifiedBy());
        assertNotNull(entity.getModifiedOn());
    }

    @Test
    void testSetModifiedDetails_WithLoggedInUser() {
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.getName()).thenReturn("jane_doe");
        baseService.setModifiedDetails(entity);
        assertNull(entity.getCreatedBy());
        assertEquals("jane_doe", entity.getModifiedBy());
        assertNotNull(entity.getModifiedOn());
    }

    @Test
    void testSoftDelete_UpdatesFlagAndModificationContext() {
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        when(mockAuthentication.getName()).thenReturn("terminator");
        entity.setDeleted(false);
        baseService.softDelete(entity);
        assertTrue(entity.getDeleted());
        assertEquals("terminator", entity.getModifiedBy());
        assertNotNull(entity.getModifiedOn());
    }

    private static class ConcreteCommonFields extends CommonFields {
    }
}
