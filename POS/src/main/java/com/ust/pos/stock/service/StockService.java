package com.ust.pos.stock.service;

import com.ust.pos.dto.StockDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StockService {

    StockDto save(StockDto stockDto);

    StockDto update(StockDto stockDto);

    void deleteByIdentifier(String identifier);

    StockDto findByIdentifier(String identifier);

    List<StockDto> findAll(Pageable pageable);

    StockDto toggleStatus(String identifier);

    List<StockDto> findIfTrue();

}
