package com.ust.pos.order.service;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.PlaceOrderRequestDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    OrderDto placeOrder(PlaceOrderRequestDto request);

    OrderDto findByIdentifier(String identifier);

    OrderDto findByOrderId(String orderId);

    List<OrderDto> findAll(Pageable pageable);

    List<OrderDto> findByCustomerIdentifier(String customerIdentifier);

    boolean delete(String identifier);
}