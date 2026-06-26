package com.ust.pos.orderentry.service;

import com.ust.pos.dto.OrderEntryDto;

import java.util.List;

public interface OrderEntryService {

    OrderEntryDto save(OrderEntryDto dto);

    List<OrderEntryDto> findByOrderId(String orderId);

    void deleteByOrderId(String orderId);
}