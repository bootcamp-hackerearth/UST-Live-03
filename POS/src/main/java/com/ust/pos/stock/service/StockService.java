package com.ust.pos.stock.service;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.StockDto;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Transactional
public interface StockService {
   StockDto save(StockDto stockDto);

   StockDto update(StockDto stockDto);

    boolean delete(String identifier);

    PageDto<StockDto> findAll(Pageable pageable);

   StockDto findByIdentifier(String identifier);

    void toggleStatus(String identifier);

    List<StockDto> findActiveStocks();
}
