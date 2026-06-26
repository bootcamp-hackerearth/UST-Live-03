package com.ust.pos.stock.service;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StockService {
    List<StockDto> findAll();

    WsDto<StockDto> findAll(Pageable pageable);

    StockDto save(StockDto stockDto);

    StockDto update(StockDto stockDto);

    void delete(String identifier);

    StockDto findByIdentifier(String identifier);

    void toggleStatus(String identifier);

    void updateStatusOnly(String identifier, Boolean status);

    Page<StockDto> findAll(String search, Pageable pageable);
}
