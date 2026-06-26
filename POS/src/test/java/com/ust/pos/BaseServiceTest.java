package com.ust.pos;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.model.CommonFields;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BaseServiceTest {

    private BaseService baseService;

    @BeforeEach
    void setUp() {
        baseService = new BaseService();
    }

    @Test
    void testSetCreatedDetails() {
        CommonFields entity = new CommonFields();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("admin");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        baseService.setCreatedDetails(entity);

        assertEquals("admin", entity.getCreatedBy());
        assertEquals("admin", entity.getModifiedBy());
        assertNotNull(entity.getCreatedOn());
        assertNotNull(entity.getModifiedOn());

        SecurityContextHolder.clearContext();
    }

    @Test
    void testSetModifiedDetails() {
        CommonFields entity = new CommonFields();

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("admin");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        baseService.setModifiedDetails(entity);

        assertEquals("admin", entity.getModifiedBy());
        assertNotNull(entity.getModifiedOn());

        SecurityContextHolder.clearContext();
    }

    @Test
    void testSoftDelete() {
        CommonFields entity = new CommonFields();
        entity.setDeleted(false);

        baseService.softDelete(entity);

        assertTrue(entity.getDeleted());
    }

    @Test
    void testSetCreatedDetails_WhenAuthenticationUnavailable() {
        CommonFields entity = new CommonFields();

        SecurityContextHolder.clearContext();

        baseService.setCreatedDetails(entity);

        assertEquals("SYSTEM", entity.getCreatedBy());
        assertEquals("SYSTEM", entity.getModifiedBy());
        assertNotNull(entity.getCreatedOn());
        assertNotNull(entity.getModifiedOn());
    }

    @Test
    void testSetModifiedDetails_WhenAuthenticationUnavailable() {
        CommonFields entity = new CommonFields();

        SecurityContextHolder.clearContext();

        baseService.setModifiedDetails(entity);

        assertEquals("SYSTEM", entity.getModifiedBy());
        assertNotNull(entity.getModifiedOn());
    }


}
