package com.ust.pos.base;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseController {

    protected Pageable getPageable(int pageNumber, int pageSize, String sortDirection, String... sort) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        List<Sort.Order> orders = new ArrayList<>();
        Arrays.stream(sort).toList().forEach(field -> {
            Sort.Order order = new Sort.Order(direction, field);
            orders.add(order);
        });
        return PageRequest.of(pageNumber, pageSize, Sort.by(orders));
    }

    protected <T> Specification<T> buildGlobalSearchSpec(Class<T> clazz, String keyword) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Class<?> current = clazz;
            while (current != null && current != Object.class) {
                for (Field field : current.getDeclaredFields()) {
                    if (field.getType().equals(String.class) && !"createdBy".equalsIgnoreCase(field.getName()) && !"modifiedBy".equalsIgnoreCase(field.getName())) {
                        predicates.add(cb.like(cb.lower(root.get(field.getName())), "%" + keyword.toLowerCase() + "%"));
                    }
                }
                current = current.getSuperclass();
            }
            Predicate deletedFalse = cb.isFalse(root.get("deleted"));
            Predicate orPredicate = cb.or(predicates.toArray(new Predicate[0]));
            return cb.and(deletedFalse, orPredicate);
        };
    }

}