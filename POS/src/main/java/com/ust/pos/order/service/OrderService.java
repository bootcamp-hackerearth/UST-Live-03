package com.ust.pos.order.service;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;


public interface OrderService {

    OrderDto save(OrderDto orderDto);

    OrderDto get(String identifier);

    OrderDto checkout(String cartId);

    WsDto<OrderDto> findAll(Pageable pageable);

    void delete(String identifier);
}