package com.ust.pos.order.service;

import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.OrdersDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Orders;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface OrderService {

    OrdersDto save(OrdersDto ordersDto);

    WsDto<OrdersDto> findAll(Pageable pageable);

    OrdersDto findByIdentifier(String identifier);

    List<OrderEntryDto> getOrderEntries(String orderId);

    String generateOrderId(String cartId);

    void delete(String identifier);

    WsDto<OrdersDto> findAll(Specification<Orders> example, Pageable pageable);
}
