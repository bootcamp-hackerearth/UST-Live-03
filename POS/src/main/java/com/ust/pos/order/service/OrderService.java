package com.ust.pos.order.service;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface OrderService {

    OrderDto placeOrder(String cartIdentifier, String paymentMode);

    OrderDto findByOrderId(String orderId);

    WsDto<OrderDto> findAll(Pageable pageable);

}