package com.ust.pos;

import com.ust.pos.common.CommonService;
import com.ust.pos.model.CommonFields;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class CommonServiceTest {

    private CommonServiceShim commonServiceShim;
    private TestEntity entity;
    private SecurityContext securityContext;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        commonServiceShim = new CommonServiceShim();
        entity = new TestEntity();
        securityContext = Mockito.mock(SecurityContextHolder.getContext().getClass());
        authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Set Audit Fields - New Entity with Authenticated User")
    void setAuditFields_NewEntity_AuthenticatedUser() {
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.when(authentication.getName()).thenReturn("testUser");

        commonServiceShim.callSetAuditFields(entity, true);

        Assertions.assertEquals("testUser", entity.getCreatedBy());
        Assertions.assertNotNull(entity.getCreatedAt());
        Assertions.assertNull(entity.getModifiedBy());
        Assertions.assertNull(entity.getModifiedAt());
    }

    @Test
    @DisplayName("Set Audit Fields - New Entity with Unauthenticated User")
    void setAuditFields_NewEntity_UnauthenticatedUser() {
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.isAuthenticated()).thenReturn(false);

        commonServiceShim.callSetAuditFields(entity, true);

        Assertions.assertEquals("system", entity.getCreatedBy());
        Assertions.assertNotNull(entity.getCreatedAt());
        Assertions.assertNull(entity.getModifiedBy());
        Assertions.assertNull(entity.getModifiedAt());
    }

    @Test
    @DisplayName("Set Audit Fields - New Entity with Null Authentication")
    void setAuditFields_NewEntity_NullAuthentication() {
        Mockito.when(securityContext.getAuthentication()).thenReturn(null);

        commonServiceShim.callSetAuditFields(entity, true);

        Assertions.assertEquals("system", entity.getCreatedBy());
        Assertions.assertNotNull(entity.getCreatedAt());
        Assertions.assertNull(entity.getModifiedBy());
        Assertions.assertNull(entity.getModifiedAt());
    }

    @Test
    @DisplayName("Set Audit Fields - Existing Entity Update")
    void setAuditFields_ExistingEntity() {
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.when(authentication.getName()).thenReturn("updaterUser");

        entity.setCreatedBy("originalCreator");
        entity.setCreatedAt(LocalDateTime.now().minusDays(1));

        commonServiceShim.callSetAuditFields(entity, false);

        Assertions.assertEquals("originalCreator", entity.getCreatedBy());
        Assertions.assertEquals("updaterUser", entity.getModifiedBy());
        Assertions.assertNotNull(entity.getModifiedAt());
    }

    @Test
    @DisplayName("Soft Delete - Success")
    void softDelete_Success() {
        entity.setDeleted(false);
        entity.setStatus(true);

        commonServiceShim.callSoftDelete(entity);

        Assertions.assertTrue(entity.isDeleted());
        Assertions.assertFalse(entity.isStatus());
    }

    private static class TestEntity extends CommonFields {
    }

    private static class CommonServiceShim extends CommonService {
        public void callSetAuditFields(CommonFields entity, boolean isNew) {
            super.setAuditFields(entity, isNew);
        }

        public void callSoftDelete(CommonFields entity) {
            super.softDelete(entity);
        }
    }
}