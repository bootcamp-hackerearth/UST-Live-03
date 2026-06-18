package com.ust.pos.orderentry.service;

import com.ust.pos.dto.OrderEntryDto;

import java.util.List;

public interface OrderEntryService {

    OrderEntryDto save(OrderEntryDto orderEntryDto);

    List<OrderEntryDto> findAllByOrderIdentifier(String orderIdentifier);

    void deleteByOrderIdentifier(String orderIdentifier);
}