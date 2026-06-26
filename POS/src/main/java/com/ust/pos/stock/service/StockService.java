package com.ust.pos.stock.service;

import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.StockDto;
import org.springframework.data.domain.Pageable;

public interface StockService {
    StockDto save(StockDto stockDto);

    StockDto update(StockDto stockDto);

    PaginationResponseDto<StockDto> findAll(Pageable pageable);

    StockDto findByIdentifier(String identifier);

    void delete(String identifier);
}
