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

    Page<StockDto> findAll(Pageable pageable, String search);

    StockDto findByIdentifier(String identifier);

    void toggleStatus(String identifier);
}
