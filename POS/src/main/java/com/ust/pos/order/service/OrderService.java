package com.ust.pos.order.service;

import com.ust.pos.dto.NodeDto;
import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Node;
import com.ust.pos.model.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface OrderService {

    OrderDto placeOrder(String cartIdentifier, String paymentMode);

    OrderDto findByOrderId(String orderId);

    WsDto<OrderDto> findAll(Pageable pageable);

    WsDto<OrderDto> findAll(Specification<Order> example, Pageable pageable, String keyword);
}