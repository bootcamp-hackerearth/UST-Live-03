package com.ust.pos.orderentry.service;

import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

public interface OrderEntryService {

    OrderEntryDto save(OrderEntryDto orderEntryDto);

    OrderEntryDto update(OrderEntryDto orderEntryDto);

    void delete(String identifier);

    WsDto<OrderEntryDto> findAll(Pageable pageable);

    OrderEntryDto findByIdentifier(String identifier);
}
