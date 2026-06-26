package com.ust.pos.order.service;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderDto checkout(OrderDto orderDto);

    OrderDto get(String identifier);

    WsDto<OrderDto> findAll(Pageable pageable);

    boolean delete(String identifier);
}