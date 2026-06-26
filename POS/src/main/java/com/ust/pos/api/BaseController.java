package com.ust.pos.api;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseController {
    protected Pageable getPageable(int pageNumber, int pageSize,
                                   String sortDirection, String... sort) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        List<Sort.Order> orders = new ArrayList<>();
        Arrays.stream(sort).forEach(field ->
                orders.add(new Sort.Order(direction, field)));
        return PageRequest.of(pageNumber, pageSize, Sort.by(orders));
    }
}
