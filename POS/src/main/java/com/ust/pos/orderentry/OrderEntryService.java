package com.ust.pos.orderentry;

import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.OrderEntry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface OrderEntryService {

    WsDto<OrderEntryDto> findAll(Pageable pageable);

    WsDto<OrderEntryDto> findAll(Specification<OrderEntry> example, Pageable pageable);

    OrderEntryDto findByIdentifier(String identifier);

    List<OrderEntryDto> findOrderEntryByOrderId(String orderId);
}
