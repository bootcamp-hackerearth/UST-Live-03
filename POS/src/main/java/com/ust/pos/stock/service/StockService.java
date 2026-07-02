package com.ust.pos.stock.service;

import com.ust.pos.dto.CustomerDto;
import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Customer;
import com.ust.pos.model.Stock;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface StockService {
    StockDto save(StockDto stockDto);

    WsDto<StockDto> findAll(Pageable pageable);

    void delete(String identifier);

    StockDto findByIdentifier(String identifier);

    StockDto update(StockDto stockDto);

    void toggleStatus(String identifier);

    WsDto<StockDto> findAll(Specification<Stock> example, Pageable pageable, String keyword);
}
