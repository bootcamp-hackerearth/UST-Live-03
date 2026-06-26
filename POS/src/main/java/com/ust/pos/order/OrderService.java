package com.ust.pos.order;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.WsDto;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;

@Transactional
public interface OrderService {

    OrderDto checkout(OrderDto orderDto);

    OrderDto get(String identifier);

    WsDto<OrderDto> findAll(Pageable pageable);

    boolean delete(String identifier);
}
