package com.ust.pos.order.service;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


public interface OrderService {
    OrderDto placeOrder(String cartIdentifier, String customerIdentifier, String paymentMethod);

    WsDto<OrderDto> findAll(Pageable pageable);

    OrderDto findByIdentifier(String identifier);

    WsDto<OrderDto> findAll(Specification<Order> example, Pageable pageable);
}