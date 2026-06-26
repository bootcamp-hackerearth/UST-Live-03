package com.ust.pos.base.service;

import com.ust.pos.model.CommonFields;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BaseService {

    private String getLoggedInUser() {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getName() == null) {
                return "SYSTEM";
            }
            return auth.getName();
        } catch (Exception e) {
            return "SYSTEM";
        }
    }    protected void setCreatedDetails(CommonFields entity) {
        entity.setCreatedBy(getLoggedInUser());
        entity.setCreatedOn(LocalDateTime.now());
    }

    protected void setModifiedDetails(CommonFields entity) {
        entity.setModifiedBy(getLoggedInUser());
        entity.setModifiedOn(LocalDateTime.now());
    }

    protected void softDelete(CommonFields entity){
        entity.setDeleted(true);
        entity.setStatus(false);
    }
}