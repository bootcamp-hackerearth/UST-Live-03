package com.ust.pos.stock.service;

import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.StockDto;
import com.ust.pos.model.Stock;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface StockService {
    StockDto save(StockDto stockDto);

    StockDto update(StockDto stockDto);

    PaginationResponseDto<StockDto> findAll(Pageable pageable);

    PaginationResponseDto<StockDto> findAll(Specification<Stock> example, Pageable pageable);

    StockDto findByIdentifier(String identifier);

    void delete(String identifier);
}
