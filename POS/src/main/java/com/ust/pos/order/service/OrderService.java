package com.ust.pos.order.service;

import com.ust.pos.dto.OrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface OrderService {
    OrderDto createOrder(String cartId, String paymentMethod);

    OrderDto updateStatus(String orderId, String status);

    OrderDto findByIdentifier(String identifier);

    List<OrderDto> findAll();

    Page<OrderDto> findAll(Pageable pageable, String search);

    void delete(String identifier);
}