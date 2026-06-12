package com.ust.pos.stock.service;

import com.ust.pos.dto.StockDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StockService {
    StockDto save(StockDto stockDto);

    StockDto update(StockDto stockDto);

    List<StockDto> findAll(Pageable pageable);

    StockDto findById(long id);

    void delete(long id);
}
