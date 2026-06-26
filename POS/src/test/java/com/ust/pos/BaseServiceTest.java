package com.ust.pos;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.modell.CommonFields;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BaseServiceTest {

    public static final String TEST_USER = "testUser";
    public static final String SYSTEM = "SYSTEM";
    private final TestBaseService service = new TestBaseService();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void setCreatedDetailsWithAuthenticatedUser() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("user");
        when(authentication.getName()).thenReturn(TEST_USER);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(context);

        CommonFields entity = mock(CommonFields.class);

        service.callSetCreatedDetails(entity);

        verify(entity).setCreatedBy(TEST_USER);
        verify(entity).setModifiedBy(TEST_USER);
        verify(entity).setCreatedOn(any(LocalDateTime.class));
        verify(entity).setModifiedOn(any(LocalDateTime.class));
    }

    @Test
    void setCreatedDetailsNullEntity() {
        service.callSetCreatedDetails(null);
    }

    @Test
    void setModifiedDetailsNullEntity() {
        service.callSetModifiedDetails(null);
    }

    @Test
    void setCreatedDetailsWithUnauthenticatedUser() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(context);

        CommonFields entity = mock(CommonFields.class);

        service.callSetCreatedDetails(entity);

        verify(entity).setCreatedBy(SYSTEM);
        verify(entity).setModifiedBy(SYSTEM);
    }

    @Test
    void setCreatedDetailsWithAnonymousUser() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(context);

        CommonFields entity = mock(CommonFields.class);

        service.callSetCreatedDetails(entity);

        verify(entity).setCreatedBy(SYSTEM);
        verify(entity).setModifiedBy(SYSTEM);
        verify(entity).setCreatedOn(any(LocalDateTime.class));
        verify(entity).setModifiedOn(any(LocalDateTime.class));
    }

    @Test
    void setCreatedDetailsWithNullAuthentication() {
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(null);

        SecurityContextHolder.setContext(context);

        CommonFields entity = mock(CommonFields.class);

        service.callSetCreatedDetails(entity);

        verify(entity).setCreatedBy(SYSTEM);
        verify(entity).setModifiedBy(SYSTEM);
        verify(entity).setCreatedOn(any(LocalDateTime.class));
        verify(entity).setModifiedOn(any(LocalDateTime.class));
    }

    @Test
    void setCreatedDetailsWhenAuthenticationThrowsException() {
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenThrow(new RuntimeException("error"));

        SecurityContextHolder.setContext(context);

        CommonFields entity = mock(CommonFields.class);

        service.callSetCreatedDetails(entity);

        verify(entity).setCreatedBy(SYSTEM);
        verify(entity).setModifiedBy(SYSTEM);
        verify(entity).setCreatedOn(any(LocalDateTime.class));
        verify(entity).setModifiedOn(any(LocalDateTime.class));
    }

    @Test
    void setCreatedDetailsWithNullEntity() {
        service.callSetCreatedDetails(null);
    }

    @Test
    void setModifiedDetailsWithAuthenticatedUser() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("user");
        when(authentication.getName()).thenReturn(TEST_USER);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(context);

        CommonFields entity = mock(CommonFields.class);

        service.callSetModifiedDetails(entity);

        verify(entity).setModifiedBy(TEST_USER);
        verify(entity).setModifiedOn(any(LocalDateTime.class));
    }

    @Test
    void setModifiedDetailsWithNullEntity() {
        service.callSetModifiedDetails(null);
    }

    @Test
    void softDeleteShouldMarkEntityDeleted() {
        CommonFields entity = mock(CommonFields.class);

        service.callSoftDelete(entity);

        verify(entity).setDeleted(true);
    }

    @Test
    void staticFieldsShouldBeInitialized() {
        assertNotNull(BaseService.CONTEXT);
    }

    static class TestBaseService extends BaseService {
        void callSetCreatedDetails(CommonFields entity) {
            setCreatedDetails(entity);
        }

        void callSetModifiedDetails(CommonFields entity) {
            setModifiedDetails(entity);
        }

        void callSoftDelete(CommonFields entity) {
            softDelete(entity);
        }
    }
}