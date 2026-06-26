package com.ust.pos.base.service;

import com.ust.pos.modell.CommonFields;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BaseService {

    public static final SecurityContext CONTEXT = SecurityContextHolder.getContext();
    public static final @Nullable Authentication AUTHENTICATION = CONTEXT.getAuthentication();

    private String getLoggedInUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null &&
                    authentication.isAuthenticated() &&
                    !"anonymousUser".equals(authentication.getPrincipal())) {
                return authentication.getName();
            }
            return "SYSTEM";
        } catch (Exception exception) {
            return "SYSTEM";
        }
    }

    protected void setCreatedDetails(CommonFields entity) {
        if (entity == null) return;

        String currentUser = getLoggedInUser();
        LocalDateTime now = LocalDateTime.now();

        entity.setCreatedBy(currentUser);
        entity.setCreatedOn(now);
        entity.setModifiedBy(currentUser);
        entity.setModifiedOn(now);
    }

    protected void setModifiedDetails(CommonFields entity) {
        if (entity == null) return;

        String currentUser = getLoggedInUser();
        LocalDateTime now = LocalDateTime.now();

        entity.setModifiedBy(currentUser);
        entity.setModifiedOn(now);
    }

    protected void softDelete(CommonFields entity) {
        entity.setDeleted(true);
    }

}