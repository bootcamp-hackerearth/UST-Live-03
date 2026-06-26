package com.ust.pos.order.service;

import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.OrdersDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    OrdersDto save(OrdersDto ordersDto);

    WsDto<OrdersDto> findAll(Pageable pageable);

    OrdersDto findByIdentifier(String identifier);

    List<OrderEntryDto> getOrderEntries(String orderId);

    String generateOrderId(String cartId);

    void delete(String identifier);
}
