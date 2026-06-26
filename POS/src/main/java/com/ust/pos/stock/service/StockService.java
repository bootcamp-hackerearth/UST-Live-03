package com.ust.pos.stock.service;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

public interface StockService {
    StockDto save(StockDto stockDto);

    WsDto<StockDto> findAll(Pageable pageable);

    StockDto findByIdentifier(String identifier);

    StockDto update(StockDto stockDto);

    StockDto toggleStatus(String identifier);

    boolean delete(String identifier);
}
