package com.ust.pos.order.service;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderDto checkout(OrderDto orderDto);

    OrderDto get(String identifier);

    WsDto<OrderDto> findAll(Pageable pageable);

    List<OrderDto> search(String query);
}
