package com.ust.pos.stock.service;

import com.ust.pos.dto.StockDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StockService {

    StockDto save(StockDto stockDto);

    StockDto update(StockDto stockDto);

    StockDto findByIdentifier(String identifier);

    List<StockDto> findAll(Pageable pageable);

    void delete(String identifier);

    StockDto toggleStatus(String identifier);
}