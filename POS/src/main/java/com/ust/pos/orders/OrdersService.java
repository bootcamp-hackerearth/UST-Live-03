package com.ust.pos.orders;

import com.ust.pos.dto.OrdersDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Orders;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface OrdersService {

    OrdersDto save(OrdersDto ordersDto);

    WsDto<OrdersDto> findAll(Pageable pageable);

    WsDto<OrdersDto> findAll(Specification<Orders> example, Pageable pageable);

    OrdersDto findByIdentifier(String identifier);
}
