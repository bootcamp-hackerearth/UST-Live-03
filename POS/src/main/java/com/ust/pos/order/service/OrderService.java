package com.ust.pos.order.service;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface OrderService {
    OrderDto checkout(OrderDto orderDto);

    OrderDto get(String identifier);

    WsDto<OrderDto> findAll(Pageable pageable);

    boolean delete(String identifier);

    WsDto<OrderDto> findAll(Specification<Order> example, Pageable pageable);
}