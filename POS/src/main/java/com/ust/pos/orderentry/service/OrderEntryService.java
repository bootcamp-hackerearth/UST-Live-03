package com.ust.pos.orderentry.service;

import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.OrdersDto;

import java.util.List;

public interface OrderEntryService {
    OrdersDto save(OrdersDto ordersDto);

    List<OrderEntryDto> findByOrderId(String orderId);
}
