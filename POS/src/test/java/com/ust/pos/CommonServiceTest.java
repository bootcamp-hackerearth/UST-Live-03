package com.ust.pos;

import com.ust.pos.model.CommonFields;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

class CommonServiceTest {
    private final CommonService commonService = new CommonService() {
    };

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void setAuditFieldsNewEntityAuthenticatedUserTest() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("john", "password", java.util.Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        TestEntity entity = new TestEntity();

        commonService.setAuditFields(entity, true);

        Assertions.assertEquals("john", entity.getCreatedBy());
        Assertions.assertEquals("john", entity.getModifiedBy());

        Assertions.assertNotNull(entity.getCreatedOn());
        Assertions.assertNotNull(entity.getModifiedOn());
    }

    @Test
    void setAuditFieldsExistingEntityAuthenticatedUserTest() {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("admin", "password", java.util.Collections.emptyList());

        SecurityContextHolder.getContext().setAuthentication(authentication);

        TestEntity entity = new TestEntity();

        entity.setCreatedBy("creator");
        entity.setCreatedOn(LocalDateTime.now().minusDays(1));

        commonService.setAuditFields(entity, false);

        Assertions.assertEquals("creator", entity.getCreatedBy());
        Assertions.assertNotNull(entity.getCreatedOn());

        Assertions.assertEquals("admin", entity.getModifiedBy());
        Assertions.assertNotNull(entity.getModifiedOn());
    }

    @Test
    void setAuditFieldsWhenAuthenticationIsNullTest() {
        SecurityContextHolder.clearContext();

        TestEntity entity = new TestEntity();

        commonService.setAuditFields(entity, true);

        Assertions.assertEquals("system", entity.getCreatedBy());
        Assertions.assertEquals("system", entity.getModifiedBy());
    }

    @Test
    void setAuditFieldsWhenAuthenticationNotAuthenticatedTest() {
        Authentication authentication = new Authentication() {

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) {
                throw new UnsupportedOperationException("Not required for this test");
            }

            @Override
            public String getName() {
                return "user";
            }

            @Override
            public Object getPrincipal() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
                return java.util.Collections.emptyList();
            }
        };
        SecurityContextHolder.getContext().setAuthentication(authentication);
        TestEntity entity = new TestEntity();

        commonService.setAuditFields(entity, true);

        Assertions.assertEquals("system", entity.getCreatedBy());
        Assertions.assertEquals("system", entity.getModifiedBy());
    }

    @Test
    void softDeleteTest() {
        TestEntity entity = new TestEntity();

        Assertions.assertFalse(entity.isDeleted());

        commonService.softDelete(entity);

        Assertions.assertTrue(entity.isDeleted());
    }
    static class TestEntity extends CommonFields {
    }
}