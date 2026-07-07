package com.ust.pos.service;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseService {

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

            Predicate deletedFalse = queryBuilder.isFalse(root.get("deleted"));
            Predicate orBlock = queryBuilder.or(orPredicates.toArray(new Predicate[0]));

            return queryBuilder.and(deletedFalse, orBlock);
        };
    }
}