package com.ust.pos.api;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BaseController {
    protected Pageable getPageable(int pageNumber, int pageSize, String sortDirection, String... sort) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        List<Sort.Order> orders = new ArrayList<>();
        Arrays.stream(sort).collect(Collectors.toSet()).forEach(field -> {
            Sort.Order order = new Sort.Order(direction, field);
            orders.add(order);
        });
        return PageRequest.of(pageNumber, pageSize, Sort.by(orders));
    }

    public <T> Specification<T> buildGlobalSearchSpec(Class<T> clazz, String keyword) {
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