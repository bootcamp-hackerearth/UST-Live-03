package com.ust.pos.stock.service;

import com.ust.pos.dto.StockDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Stock;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface StockService {

    StockDto save(StockDto stockDto);

    WsDto<StockDto> findAll(Pageable pageable);

    WsDto<StockDto> findAll(Specification<Stock>example, Pageable pageable);

    void delete(String identifier);

    StockDto findByIdentifier(String identifier);

    StockDto update(StockDto stockDto);

    StockDto changeToggleStatus(String identifier, boolean status);

    List<StockDto> findActiveStatus();
}
