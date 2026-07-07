package com.ust.pos.order.service;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface OrderService {

    OrderDto placeOrder(String customerId, String paymentMethod);

    PaginatedResponseDto<OrderDto> findAll(Pageable pageable);

    OrderDto findByIdentifier(String identifier);

    OrderDto findById(Long id);

    PaginatedResponseDto<OrderDto> findAll(Specification<Order> example, Pageable pageable);
}