package com.ust.pos.stock.service;

import com.ust.pos.dto.StockDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StockService {

    StockDto createStock(StockDto stockDto);

    StockDto updateStockQuantity(Long stockId, Integer quantity);

    StockDto getStock(Long productId, Long warehouseId);

    List<StockDto> findAll(Pageable pageable);

    boolean deleteStock(Long stockId);

    void toggleStatus(Long stockId);

}