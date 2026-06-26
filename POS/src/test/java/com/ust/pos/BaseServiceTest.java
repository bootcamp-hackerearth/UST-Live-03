package com.ust.pos;

import com.ust.pos.base.service.BaseService;
import com.ust.pos.model.CommonFields;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class BaseServiceTest {

    private BaseService baseService;

    private static class TestEntity extends CommonFields {}

    @BeforeEach
    void setUp() {
        baseService = new BaseService();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void setCreatedDetails_WithAnonymousUser_SetsSystemDefaults() {
        TestEntity entity = new TestEntity();

        baseService.setCreatedDetails(entity);

        Assertions.assertEquals("SYSTEM", entity.getCreatedBy());
        Assertions.assertEquals("SYSTEM", entity.getModifiedBy());
        Assertions.assertNotNull(entity.getCreatedOn());
        Assertions.assertNotNull(entity.getModifiedOn());
    }

    @Test
    void setCreatedDetails_WithAuthenticatedUser_SetsUsername() {
        TestEntity entity = new TestEntity();

        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.when(authentication.getName()).thenReturn("harsha_admin");

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        baseService.setCreatedDetails(entity);

        Assertions.assertEquals("harsha_admin", entity.getCreatedBy());
        Assertions.assertEquals("harsha_admin", entity.getModifiedBy());
    }

    @Test
    void setModifiedDetails_UpdatesOnlyModifiedProperties() {
        TestEntity entity = new TestEntity();
        entity.setCreatedBy("ORIGINAL_CREATOR");

        baseService.setModifiedDetails(entity);

        Assertions.assertEquals("ORIGINAL_CREATOR", entity.getCreatedBy());
        Assertions.assertEquals("SYSTEM", entity.getModifiedBy());
        Assertions.assertNotNull(entity.getModifiedOn());
    }

    @Test
    void softDelete_SetsDeletedToTrue() {
        TestEntity entity = new TestEntity();
        entity.setDeleted(false);

        baseService.softDelete(entity);

        Assertions.assertTrue(entity.isDeleted());
    }
}