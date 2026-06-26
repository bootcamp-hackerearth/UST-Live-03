package com.ust.pos.base.service;

import com.ust.pos.model.CommonFields;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BaseService {

    private String getLoggedInUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return "SYSTEM";
        }

        return authentication.getName();
    }

    protected void setCreatedDetails(CommonFields entity) {
        String user = getLoggedInUser();

        entity.setCreatedBy(user);
        entity.setCreatedOn(LocalDateTime.now());
        entity.setModifiedBy(user);
        entity.setModifiedOn(LocalDateTime.now());
    }

    protected void setModifiedDetails(CommonFields entity) {
        entity.setModifiedBy(getLoggedInUser());
        entity.setModifiedOn(LocalDateTime.now());
    }

    protected void softDelete(CommonFields entity) {
        entity.setDeleted(true);
        entity.setStatus(false);
    }

    protected boolean isSoftDeleted(CommonFields entity) {
        return entity != null && entity.isDeleted();
    }

    protected String getDeletedMessage(String entityName, String identifier) {
        return entityName + " " + identifier +
                " has been deleted. Please contact the administrator.";
    }
}