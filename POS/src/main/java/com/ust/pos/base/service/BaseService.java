package com.ust.pos.base.service;

import com.ust.pos.modell.CommonFields;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BaseService {

    private String getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return "SYSTEM";
        }
        return authentication.getName();
    }

    protected void setCreatedDetails(CommonFields entity) {

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

    protected void setModifiedDetails(CommonFields entity) {

        if (entity == null) {
            return;
        }
        String currentUser = getLoggedInUser();
        LocalDateTime now = LocalDateTime.now();

        entity.setModifiedBy(currentUser);
        entity.setModifiedOn(now);
    }

    protected void softDelete(CommonFields entity){
        entity.setDeleted(true);
    }
}