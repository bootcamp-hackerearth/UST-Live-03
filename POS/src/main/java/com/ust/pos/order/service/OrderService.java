package com.ust.pos.order.service;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.PlaceOrderRequestDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Orders;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface OrderService {

    OrderDto placeOrder(PlaceOrderRequestDto request);

    OrderDto findByIdentifier(String identifier);

    OrderDto findByOrderId(String orderId);

    List<OrderDto> findByCustomerIdentifier(String customerIdentifier);

    boolean delete(String identifier);

    WsDto<OrderDto> findAll(Pageable pageable);

    WsDto<OrderDto> findAll(Specification<Orders> spec, Pageable pageable, String keyword);
}