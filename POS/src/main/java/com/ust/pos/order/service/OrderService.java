package com.ust.pos.order.service;

import com.ust.pos.dto.OrdersDto;

import java.util.List;

public interface OrderService {
    List<OrdersDto> findAll();
    OrdersDto findByIdentifier(String identifier);
}
