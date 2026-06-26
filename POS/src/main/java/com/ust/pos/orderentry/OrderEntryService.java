package com.ust.pos.orderentry;

import com.ust.pos.dto.OrderEntryDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderEntryService {

    WsDto<OrderEntryDto> findAll(Pageable pageable);

    OrderEntryDto findByIdentifier(String identifier);

    List<OrderEntryDto> findOrderEntryByOrderId(String orderId);
}
