package com.ust.pos.order.service;

import com.ust.pos.dto.OrderDto;

import java.util.List;

public interface OrderService {
    OrderDto placeOrder(String cartIdentifier);

    OrderDto findByIdentifier(String identifier);

    List<OrderDto> findAllByCustomer(String customer);

    void delete(String identifier);

    OrderDto cancelOrder(String identifier);

    List<OrderDto> findAll();
}