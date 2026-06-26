package com.ust.pos.orders.service;

import com.ust.pos.dto.OrdersDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

public interface OrdersService {

    OrdersDto save(OrdersDto ordersDto);

    OrdersDto get(String identifier);

    void delete(String identifier);

    WsDto<OrdersDto> findAll(Pageable pageable);
}