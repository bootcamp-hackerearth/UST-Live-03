package com.ust.pos;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.model.CommonFields;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

class BaseServiceTest {
    private final BaseService baseService = new BaseService();

    @Test
    void setCreatedDetailsTest() {
        CommonFields entity = new CommonFields();
        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.isAuthenticated()).thenReturn(true);
        Mockito.when(auth.getName()).thenReturn("admin");
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
        baseService.setCreatedDetails(entity);
        Assertions.assertEquals("admin", entity.getCreatedBy());
        Assertions.assertEquals("admin", entity.getModifiedBy());
        Assertions.assertNotNull(entity.getCreatedOn());
        Assertions.assertNotNull(entity.getModifiedOn());
    }

    @Test
    void setCreatedDetailsNullEntityTest() {
        baseService.setCreatedDetails(null);
        Assertions.assertTrue(true);
    }

    @Test
    void setModifiedDetailsTest() {
        CommonFields entity = new CommonFields();
        Authentication auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.isAuthenticated()).thenReturn(true);
        Mockito.when(auth.getName()).thenReturn("user1");
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);
        baseService.setModifiedDetails(entity);
        Assertions.assertEquals("user1", entity.getModifiedBy());
        Assertions.assertNotNull(entity.getModifiedOn());
    }

    @Test
    void setModifiedDetailsNullEntityTest() {
        baseService.setModifiedDetails(null);
        Assertions.assertTrue(true);
    }

    @Test
    void getLoggedInUserWhenNoAuthTest() {
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(context);
        CommonFields entity = new CommonFields();
        baseService.setCreatedDetails(entity);
        Assertions.assertEquals("SYSTEM", entity.getCreatedBy());
    }

    @Test
    void getLoggedInUserExceptionTest() {
        SecurityContext context = Mockito.mock(SecurityContext.class);
        Mockito.when(context.getAuthentication()).thenThrow(new RuntimeException());
        SecurityContextHolder.setContext(context);
        CommonFields entity = new CommonFields();
        baseService.setCreatedDetails(entity);
        Assertions.assertEquals("SYSTEM", entity.getCreatedBy());
    }

    @Test
    void softDeleteTest() {
        CommonFields entity = new CommonFields();
        entity.setDeleted(false);
        baseService.softDelete(entity);
        Assertions.assertTrue(entity.getDeleted());
    }
}