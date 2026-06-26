package com.ust.pos.order.service;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.PaginatedResponseDto;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderDto placeOrder(String customerId, String paymentMethod);

    PaginatedResponseDto<OrderDto> findAll(Pageable pageable);

    OrderDto findByIdentifier(String identifier);

    OrderDto findById(Long id);
}