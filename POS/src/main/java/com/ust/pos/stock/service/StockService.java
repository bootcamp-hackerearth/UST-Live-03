package com.ust.pos.stock.service;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Stock;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface StockService {

    StockDto save(StockDto stockDto);

    StockDto update(StockDto stockDto);

    void deleteByIdentifier(String identifier);

    StockDto findByIdentifier(String identifier);

    WsDto<StockDto> findAll(Pageable pageable);

    StockDto toggleStatus(String identifier);

    List<StockDto> findIfTrue();

    WsDto<StockDto> findAll(Specification<Stock> example, Pageable pageable);

}
