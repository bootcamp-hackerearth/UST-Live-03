package com.ust.pos.base.service;

import com.ust.pos.model.CommonFields;
import org.springframework.data.domain.ExampleMatcher;
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

    public void setCreatedDetails(CommonFields entity) {
        entity.setCreatedBy(getLoggedInUser());
        entity.setCreatedOn(LocalDateTime.now());
        entity.setModifiedBy(getLoggedInUser());
        entity.setModifiedOn(LocalDateTime.now());
    }

    public void setModifiedDetails(CommonFields entity) {
        entity.setModifiedBy(getLoggedInUser());
        entity.setModifiedOn(LocalDateTime.now());
    }

    public void softDelete(CommonFields entity) {
        entity.setDeleted(true);
    }

    protected ExampleMatcher getGlobalSearchMatcher() {
        return ExampleMatcher.matchingAny()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnorePaths(
                        "id",
                        "deleted",
                        "createdBy",
                        "createdOn",
                        "modifiedBy",
                        "modifiedOn"
                );
    }
}
