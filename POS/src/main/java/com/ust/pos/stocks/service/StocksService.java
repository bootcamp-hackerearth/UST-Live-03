package com.ust.pos.stocks.service;

import com.ust.pos.dto.StocksDto;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface StocksService {
    List<StocksDto> findAll(Pageable pageable);

    StocksDto save(StocksDto stocksDto);

    StocksDto update(StocksDto stocksDto);

    boolean delete(String identifier);

    StocksDto findByIdentifier(String identifer);

    List<StocksDto> findIfTrue();

    StocksDto toggleStatus(String identifier);

}