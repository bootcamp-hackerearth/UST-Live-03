package com.ust.pos.ordersentry.service;

import com.ust.pos.dto.OrdersEntryDto;

import java.util.List;

public interface OrdersEntryService {

    OrdersEntryDto save(OrdersEntryDto ordersEntryDto);

    OrdersEntryDto get(String identifier);

    void delete(String identifier);

    List<OrdersEntryDto> findByOrdersId(String ordersId);

    void deleteByOrdersId(String ordersId);
}