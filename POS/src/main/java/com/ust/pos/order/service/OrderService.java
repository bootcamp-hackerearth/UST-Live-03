package com.ust.pos.order.service;

import com.ust.pos.dto.OrderDto;

import java.util.List;

public interface OrderService {
    String generateOrderId(String identifier);

    OrderDto placeOrder(String identifier, String paymentMode);

    List<OrderDto> findAll();
}
