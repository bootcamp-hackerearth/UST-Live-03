package com.ust.pos.stock.service;

import com.ust.pos.dto.StockDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StockService {
    StockDto save(StockDto userDto);

    StockDto update(StockDto userDto);

    void delete(String identifier);

    List<StockDto> findAll();

    StockDto findByIdentifier(String identifier);

    StockDto toggleStatus(String identifier);

    Page<StockDto> findAll(Pageable pageable, String search);
}
