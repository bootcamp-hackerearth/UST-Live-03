package com.ust.pos.base.service;

import com.ust.pos.model.CommonFields;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.ust.pos.dto.WsDto;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.stream.Collectors;
import java.util.List;

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