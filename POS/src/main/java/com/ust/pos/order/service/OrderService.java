package com.ust.pos.order.service;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;


public interface OrderService {
    OrderDto placeOrder(String cartIdentifier, String customerIdentifier, String paymentMethod);

    WsDto<OrderDto> findAll(Pageable pageable);

    OrderDto findByIdentifier(String identifier);
}