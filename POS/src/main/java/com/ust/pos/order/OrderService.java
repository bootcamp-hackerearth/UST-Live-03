package com.ust.pos.order;

import com.ust.pos.dto.OrderDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Order;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Transactional
public interface OrderService {

    OrderDto checkout(OrderDto orderDto);

    OrderDto get(String identifier);

    WsDto<OrderDto> findAll(Pageable pageable);

    boolean delete(String identifier);

    WsDto<OrderDto> findAll(Specification<Order> example, Pageable pageable);
}
