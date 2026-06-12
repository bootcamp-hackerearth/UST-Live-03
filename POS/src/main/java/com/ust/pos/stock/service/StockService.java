package com.ust.pos.stock.service;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

public interface StockService {

    StockDto save(StockDto stockDto);

    WsDto<StockDto> findAll(Pageable pageable);

    StockDto update(StockDto stockDto);

    StockDto findByIdentifier(String identifier);

    void delete(String identifier);

    StockDto toggleStatus(String identifier, boolean status);

}