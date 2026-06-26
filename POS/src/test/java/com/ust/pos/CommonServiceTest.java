package com.ust.pos;

import com.ust.pos.common.CommonService;
import com.ust.pos.model.CommonFields;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommonServiceTest {

    private CommonServiceTestImpl commonService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private TestEntity testEntity;

    private static class TestEntity extends CommonFields {}

    private static class CommonServiceTestImpl extends CommonService {
        public void callSetAuditFields(CommonFields entity, boolean isNew) {
            setAuditFields(entity, isNew);
        }
        public void callSoftDelete(CommonFields entity) {
            softDelete(entity);
        }
    }

    @BeforeEach
    void setUp() {
        commonService = new CommonServiceTestImpl();
        testEntity = new TestEntity();
    }

    @Test
    @DisplayName("Set Audit Fields - New Entity with Authenticated User")
    void setAuditFields_NewEntity_AuthenticatedUser() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getName()).thenReturn("adminUser");

            commonService.callSetAuditFields(testEntity, true);

            Assertions.assertEquals("adminUser", testEntity.getCreatedBy());
            Assertions.assertNotNull(testEntity.getCreatedAt());
            Assertions.assertNull(testEntity.getModifiedBy());
            Assertions.assertNull(testEntity.getModifiedAt());
        }
    }

    @Test
    @DisplayName("Set Audit Fields - New Entity with System Fallback (Null Authentication)")
    void setAuditFields_NewEntity_SystemFallback_NullAuth() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(null);

            commonService.callSetAuditFields(testEntity, true);

            Assertions.assertEquals("system", testEntity.getCreatedBy());
            Assertions.assertNotNull(testEntity.getCreatedAt());
        }
    }

    @Test
    @DisplayName("Set Audit Fields - New Entity with System Fallback (Unauthenticated)")
    void setAuditFields_NewEntity_SystemFallback_Unauthenticated() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(false);

            commonService.callSetAuditFields(testEntity, true);

            Assertions.assertEquals("system", testEntity.getCreatedBy());
            Assertions.assertNotNull(testEntity.getCreatedAt());
        }
    }

    @Test
    @DisplayName("Set Audit Fields - Existing Entity Update")
    void setAuditFields_ExistingEntity() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getName()).thenReturn("updateUser");

            testEntity.setCreatedBy("originalCreator");

            commonService.callSetAuditFields(testEntity, false);

            Assertions.assertEquals("originalCreator", testEntity.getCreatedBy());
            Assertions.assertEquals("updateUser", testEntity.getModifiedBy());
            Assertions.assertNotNull(testEntity.getModifiedAt());
        }
    }

    @Test
    @DisplayName("Soft Delete - Sets Flags Correctly")
    void softDelete_Success() {
        testEntity.setDeleted(false);
        testEntity.setStatus(true);

        commonService.callSoftDelete(testEntity);

        Assertions.assertTrue(testEntity.isDeleted());
        Assertions.assertFalse(testEntity.isStatus());
    }
}