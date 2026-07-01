package com.ust.pos.base.service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class GenericSpecification {

    private GenericSpecification() {
    }

    public static <T> Specification<T> search(String keyword, List<String> searchFields) {
        return (root, query, cb) -> {
            Predicate notDeleted = cb.isFalse(root.get("deleted"));

            if (keyword == null || keyword.trim().isEmpty() || searchFields == null || searchFields.isEmpty()) {
                return notDeleted;
            }

            String pattern = "%" + keyword.trim().toLowerCase() + "%";

            Predicate[] fieldPredicates = searchFields.stream()
                    .map(field -> cb.like(cb.lower(root.get(field)), pattern))
                    .toArray(Predicate[]::new);

            return cb.and(notDeleted, cb.or(fieldPredicates));
        };
    }
}