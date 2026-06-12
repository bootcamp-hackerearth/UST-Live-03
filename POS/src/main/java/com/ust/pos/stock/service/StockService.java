package com.ust.pos.stock.service;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.StockDto;
import org.springframework.data.domain.Pageable;


public interface StockService {
   StockDto save(StockDto stockDto);

   StockDto update(StockDto stockDto);

    void delete(String identifier);

    PageDto<StockDto> findAll(Pageable pageable);

   StockDto findByIdentifier(String identifier);

    void toggleStatus(String identifier);
}
