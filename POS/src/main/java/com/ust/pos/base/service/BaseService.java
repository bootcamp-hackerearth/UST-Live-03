package com.ust.pos.base.service;

import com.ust.pos.model.CommonFields;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class BaseService {


    private String getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : "SYSTEM";
    }

    public void setCreatedDetails(CommonFields entity) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
        entity.setCreatedBy(getLoggedInUser());
        entity.setCreatedOn(now);
        entity.setModifiedBy(getLoggedInUser());
        entity.setModifiedOn(now);
    }

    public void setModifiedDetails(CommonFields entity) {
        entity.setModifiedBy(getLoggedInUser());
        entity.setModifiedOn(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
    }

    public void softDelete(CommonFields entity) {
        entity.setDeleted(true);
        setModifiedDetails(entity);
    }
}
