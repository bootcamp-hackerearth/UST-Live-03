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
        try {
            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            return authentication != null
                    ? authentication.getName()
                    : "SYSTEM";
        } catch (Exception e) {
            return "SYSTEM";
        }
    }

    protected void setCreatedDetails(CommonFields entity) {
        entity.setCreatedBy(getLoggedInUser());
        entity.setCreatedOn(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
        entity.setModifiedBy(getLoggedInUser());
        entity.setModifiedOn(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
    }

    protected void setModifiedDetails(CommonFields entity) {
        entity.setModifiedBy(getLoggedInUser());
        entity.setModifiedOn(LocalDateTime.now(ZoneId.of("Asia/Kolkata")));
    }

    protected void softDelete(CommonFields entity) {
        entity.setDeleted(true);
    }

}