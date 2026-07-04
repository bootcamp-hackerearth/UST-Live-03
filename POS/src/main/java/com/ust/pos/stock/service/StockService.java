package com.ust.pos.stock.service;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Stock;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


public interface StockService {

    StockDto createStock(StockDto stockDto);

    StockDto updateStockQuantity(Long stockId, Integer quantity);

    StockDto getStock(Long productId, Long warehouseId);

    boolean deleteStock(Long stockId);

    void toggleStatus(Long stockId);

    WsDto<StockDto> findAll(Pageable pageable);

    WsDto<StockDto> findAll(Specification<Stock> spec, Pageable pageable, String keyword);
}