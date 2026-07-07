package com.ust.pos.api;

import org.springframework.data.domain.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseController {

    protected Pageable getPageable(int pageNumber, int pageSize, String sortDirection, String... sort) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        List<Sort.Order> orders = new ArrayList<>();
        Arrays.stream(sort).forEach(field -> orders.add(new Sort.Order(direction, field)));
        return PageRequest.of(pageNumber, pageSize, Sort.by(orders));
    }

    protected <T> Example<T> buildSearchProbe(Class<T> clazz, String keyword, String... extraIgnoredPaths)
            throws ReflectiveOperationException {
        T probe = clazz.getDeclaredConstructor().newInstance();
        if (keyword != null && !keyword.isBlank()) {
            populateProbeFields(clazz, probe, keyword);
        }
        return Example.of(probe, buildMatcher(extraIgnoredPaths));
    }

    private <T> void populateProbeFields(Class<T> clazz, T probe, String keyword) throws IllegalAccessException {
        boolean isNumeric = keyword.matches("\\d+");
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                assignFieldValue(field, probe, keyword, isNumeric);
            }
            current = current.getSuperclass();
        }
    }

    private void assignFieldValue(Field field, Object probe, String keyword, boolean isNumeric)
            throws IllegalAccessException {
        Class<?> type = field.getType();
        Object value = null;

        if (type.equals(String.class)) {
            value = keyword;
        } else if (isNumeric && type.equals(Long.class)) {
            value = Long.valueOf(keyword);
        } else if (isNumeric && type.equals(Integer.class)) {
            value = Integer.valueOf(keyword);
        }

        if (value != null) {
            // Reflection access is intentional and scoped: only String/Long/Integer
            // fields on the probe class are touched, and only to populate a search
            // example — no external input controls *which* field is written.
            field.setAccessible(true); //NOSONAR - required for generic reflective probe population
            field.set(probe, value);
        }
    }

    private ExampleMatcher buildMatcher(String... extraIgnoredPaths) {
        List<String> ignoredPaths = new ArrayList<>(
                Arrays.asList("id", "createdOn", "modifiedOn", "createdBy", "modifiedBy", "deleted", "status"));
        ignoredPaths.addAll(Arrays.asList(extraIgnoredPaths));

        return ExampleMatcher.matchingAny()
                .withIgnoreCase()
                .withIgnoreNullValues()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                .withIgnorePaths(ignoredPaths.toArray(new String[0]));
    }
}