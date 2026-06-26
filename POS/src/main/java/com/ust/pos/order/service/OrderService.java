package com.ust.pos.order.service;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    OrderDto save(OrderDto orderDto);

    OrderDto update(OrderDto orderDto);

    Page<OrderDto> findAll(String search, Pageable pageable);

    void delete(String identifier);

    List<OrderDto> findAll();

    WsDto<OrderDto> findAll(Pageable pageable);

    OrderDto findByIdentifier(String identifier);

    OrderDto placeOrder(OrderDto orderDto);

    List<OrderDto> findByCustomerEmail(String email);
}