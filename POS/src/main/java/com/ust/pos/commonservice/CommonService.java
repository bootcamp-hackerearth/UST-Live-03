package com.ust.pos.commonservice;

import com.ust.pos.model.CommonFields;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class CommonService {

    protected void setAuditFields(CommonFields entity, boolean isNew) {
        String currentUser = getCurrentUsername();
        LocalDateTime now = LocalDateTime.now();

        if (isNew) {
            entity.setCreatedBy(currentUser);
            entity.setCreatedOn(now);
            entity.setModifiedBy(null);
            entity.setModifiedOn(null);
        } else {
            entity.setModifiedBy(currentUser);
            entity.setModifiedOn(now);
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
        entity.setDelete(true);
        entity.setStatus(false);
    }

    protected <T> Specification<T> buildGlobalSearchSpec(Class<T> clazz, String keyword) {

        return (root, query, queryBuilder) -> {

            List<Predicate> orPredicates = new ArrayList<>();
            Class<?> current = clazz;

            while (current != null && current != Object.class) {

                for (Field field : current.getDeclaredFields()) {

                    if (field.getType().equals(String.class)
                            && !field.getName().equalsIgnoreCase("createdBy")
                            && !field.getName().equalsIgnoreCase("modifiedBy")) {

                        orPredicates.add(
                                queryBuilder.like(
                                        queryBuilder.lower(root.get(field.getName())),
                                        "%" + keyword.toLowerCase() + "%"
                                )
                        );
                    }
                }

                current = current.getSuperclass();
            }

            Predicate deletedFalse = queryBuilder.isFalse(root.get("isDelete"));
            Predicate orBlock = queryBuilder.or(orPredicates.toArray(new Predicate[0]));

            return queryBuilder.and(deletedFalse, orBlock);
        };
    }
}