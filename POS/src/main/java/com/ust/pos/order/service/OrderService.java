package com.ust.pos.order.service;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.PaginationResponseDto;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto checkout(OrderDto orderDto);

    OrderDto get(String identifier);

    PaginationResponseDto<OrderDto> findAll(Pageable pageable);
}
