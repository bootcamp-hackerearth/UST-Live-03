package com.ust.pos.stock.service;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StockService {
    WsDto<StockDto> findAll(Pageable pageable);

    StockDto findByIdentifier(String identifier);

    StockDto save(StockDto stockDto);

    void delete(String identifier);

    StockDto update(StockDto stockDto);
}
