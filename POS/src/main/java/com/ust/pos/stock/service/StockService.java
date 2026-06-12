package com.ust.pos.stock.service;

import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.StockDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StockService {

    StockDto findByIdentifier(String identifier);

    StockDto save(StockDto stockDto);

    StockDto update(StockDto stockDto);

    void delete(String identifier);

    PaginatedResponseDto<StockDto> findAll(Pageable pageable);

    List<StockDto> findAllActive();

    void changeStatus(String identifier, boolean status);
}
