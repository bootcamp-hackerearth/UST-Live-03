package com.ust.pos;

import com.ust.pos.commonservice.CommonService;
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
class CommonServiceTest {

    private CommonService commonService;

    private SecurityContext securityContext;

    private Authentication authentication;

    private static class TestEntity extends CommonFields {
    }

    private static class CommonServiceImpl extends CommonService {
        public void triggerSetAuditFields(CommonFields entity, boolean isNew) {
            setAuditFields(entity, isNew);
        }

        public void triggerSoftDelete(CommonFields entity) {
            softDelete(entity);
        }
    }

    @BeforeEach
    void setUp() {
        commonService = new CommonServiceImpl();
        securityContext = Mockito.mock(SecurityContext.class);
        authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void setAuditFieldsNewTest() {
        TestEntity entity = new TestEntity();
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.when(authentication.getName()).thenReturn("Admin");

        ((CommonServiceImpl) commonService).triggerSetAuditFields(entity, true);

        Assertions.assertEquals("Admin", entity.getCreatedBy());
        Assertions.assertNotNull(entity.getCreatedOn());
        Assertions.assertNull(entity.getModifiedBy());
        Assertions.assertNull(entity.getModifiedOn());
    }

    @Test
    void setAuditFieldsUpdateTest() {
        TestEntity entity = new TestEntity();
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.when(authentication.getName()).thenReturn("Admin");

        ((CommonServiceImpl) commonService).triggerSetAuditFields(entity, false);

        Assertions.assertEquals("Admin", entity.getModifiedBy());
        Assertions.assertNotNull(entity.getModifiedOn());
        Assertions.assertNull(entity.getCreatedBy());
        Assertions.assertNull(entity.getCreatedOn());
    }

    @Test
    void setAuditFieldsSystemUserTest() {
        TestEntity entity = new TestEntity();
        Mockito.when(securityContext.getAuthentication()).thenReturn(null);

        ((CommonServiceImpl) commonService).triggerSetAuditFields(entity, true);

        Assertions.assertEquals("system", entity.getCreatedBy());
    }

    @Test
    void setAuditFieldsUnauthenticatedTest() {
        TestEntity entity = new TestEntity();
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.isAuthenticated()).thenReturn(false);

        ((CommonServiceImpl) commonService).triggerSetAuditFields(entity, true);

        Assertions.assertEquals("system", entity.getCreatedBy());
    }

    @Test
    void softDeleteTest() {
        TestEntity entity = new TestEntity();
        entity.setDeleted(false);
        entity.setStatus(true);

        ((CommonServiceImpl) commonService).triggerSoftDelete(entity);

        Assertions.assertTrue(entity.isDeleted());
        Assertions.assertFalse(entity.isStatus());
    }
}