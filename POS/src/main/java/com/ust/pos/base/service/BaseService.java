package com.ust.pos.base.service;

import com.ust.pos.model.CommonFields;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BaseService {
    private String getLoggedInUser() {
        try {
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                return authentication.getName();
            }
        } catch (Exception e) {
            return "SYSTEM";
        }
        return "SYSTEM";
    }

    public void setCreatedDetails(CommonFields entity) {
        if (entity == null) {
            return;
        }
        String currentUser = getLoggedInUser();
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedBy(currentUser);
        entity.setCreatedOn(now);
        entity.setModifiedBy(currentUser);
        entity.setModifiedOn(now);
    }

    public void setModifiedDetails(CommonFields entity) {
        if (entity == null) {
            return;
        }
        String currentUser = getLoggedInUser();
        LocalDateTime now = LocalDateTime.now();
        entity.setModifiedBy(currentUser);
        entity.setModifiedOn(now);
    }

    public void softDelete(CommonFields entity) {
        if (entity == null) {
            return;
        }
        entity.setDeleted(true);
    }
}