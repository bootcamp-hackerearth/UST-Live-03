package com.ust.pos.stock.service;

import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.StockDto;
import com.ust.pos.model.Stock;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface StockService {

    StockDto save(StockDto stockDto);

    PaginationResponseDto<StockDto> findAll(Pageable pageable);

    StockDto update(StockDto stockDto);

    StockDto findByIdentifier(String identifier);

    void delete(String identifier);

    StockDto toggleStatus(String identifier,boolean status);

    PaginationResponseDto<StockDto> findAll(Specification<Stock> example, Pageable pageable);
}