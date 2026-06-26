package com.ust.pos.orders.service;

import com.ust.pos.dto.OrdersDto;

import java.util.List;

public interface OrdersService {
    String generateOrderId(String cartIdentifier);

    OrdersDto placeOrder(String cartIdentifier, String paymentMode);

    List<OrdersDto> findAll();
}