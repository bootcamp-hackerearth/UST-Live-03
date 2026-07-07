package com.ust.pos.stock.service;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.models.Stock;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface StockService {
    StockDto findByIdentifier(String identifier);

    StockDto findById(Long id);

    StockDto save(StockDto stockDto);

    StockDto update(StockDto stockDto);

    void delete(String identifier);

    WsDto<StockDto> findAll(Pageable pageable);

    WsDto<StockDto> findAll(Specification<Stock> example, Pageable pageable);

}
