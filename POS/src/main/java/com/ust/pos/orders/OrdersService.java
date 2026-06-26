package com.ust.pos.orders;

import com.ust.pos.dto.OrdersDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

public interface OrdersService {

    OrdersDto save(OrdersDto ordersDto);

    WsDto<OrdersDto> findAll(Pageable pageable);

    OrdersDto findByIdentifier(String identifier);
}
