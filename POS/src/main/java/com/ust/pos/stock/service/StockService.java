package com.ust.pos.stock.service;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StockService {

    StockDto save(StockDto stockDto);

    StockDto update(StockDto stockDto);

    StockDto findByIdentifier(String identifier);

    WsDto<StockDto> findAll(Pageable pageable);

    void delete(String identifier);

    StockDto toggleStatus(String identifier);

    List<StockDto> findActiveStock();

    boolean isStockAvailable(String productIdentifier, Integer quantity);

    StockDto reduceStock(String productIdentifier, Integer quantity);
}