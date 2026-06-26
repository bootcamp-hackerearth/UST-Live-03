package com.ust.pos.orderentry;

import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderEntryService {

    OrderEntryDto save(OrderEntryDto orderEntryDto);

    OrderEntryDto update(OrderEntryDto orderEntryDto);

    void delete(String identifier);

    List<OrderEntryDto> findAll();

    WsDto<OrderEntryDto> findAll(Pageable pageable);

    OrderEntryDto findByIdentifier(String identifier);

    List<OrderEntryDto> findByOrderId(String orderId);
}