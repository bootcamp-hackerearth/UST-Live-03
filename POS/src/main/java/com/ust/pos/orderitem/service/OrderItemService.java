package com.ust.pos.orderitem.service;

import com.ust.pos.dto.OrderItemDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderItemService {

    OrderItemDto save(OrderItemDto dto);

    OrderItemDto update(OrderItemDto dto);

    void delete(String identifier);

    OrderItemDto findByIdentifier(String identifier);

    List<OrderItemDto> findAll();

    List<OrderItemDto> findAll(Pageable pageable);

    List<OrderItemDto> findByOrderIdentifier(String orderIdentifier);
}