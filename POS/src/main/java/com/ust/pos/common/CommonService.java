package com.ust.pos.common;

import com.ust.pos.model.CommonFields;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

public abstract class CommonService {

    protected void setAuditFields(CommonFields entity, boolean isNew) {
        String currentUser = getCurrentUsername();
        LocalDateTime now = LocalDateTime.now();

        if (isNew) {
            entity.setCreatedBy(currentUser);
            entity.setCreatedAt(now);
            entity.setModifiedBy(null);
            entity.setModifiedAt(null);
        } else {
            entity.setModifiedBy(currentUser);
            entity.setModifiedAt(now);
        }
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "system";
        }
        return authentication.getName();
    }

    protected void softDelete(CommonFields entity) {
        entity.setDeleted(true);
        entity.setStatus(false);
    }
}