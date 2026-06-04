package com.ust.pos.stock.service;

import com.ust.pos.dto.StockDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StockService {

    StockDto save(StockDto stockDto);

    StockDto update(StockDto stockDto);

    void delete(Long id);

    List<StockDto> findAll(Pageable pageable);

    StockDto findById(Long id);

    StockDto changeStockStatus(String identifier, boolean status);

    StockDto findByIdentifier(String identifier);
}
