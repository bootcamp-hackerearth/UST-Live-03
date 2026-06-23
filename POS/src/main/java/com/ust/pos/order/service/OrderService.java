package com.ust.pos.order.service;

import com.ust.pos.dto.OrderDto;

import java.util.List;

public interface OrderService {
    OrderDto processCheckout(OrderDto orderDto);

    OrderDto getOrderDetails(String identifier);

    List<OrderDto> getAllOrdersList();
}