package com.ust.pos.stock.service;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Stock;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface StockService {
    WsDto<StockDto> findAll(Pageable pageable);

    StockDto findByIdentifier(String identifier);

    StockDto save(StockDto stockDto);

    void delete(String identifier);

    StockDto update(StockDto stockDto);

    void toggleStatus(String identifier);

    WsDto<StockDto> findAll(Specification<Stock> example, Pageable pageable);
}
