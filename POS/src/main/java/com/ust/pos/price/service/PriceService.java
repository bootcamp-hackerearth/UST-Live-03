package com.ust.pos.price.service;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface PriceService {
    PriceDto save(PriceDto priceDto);

    PriceDto update(PriceDto priceDto);

    boolean delete(String identifier);

    WsDto<PriceDto> findAll(Pageable pageable);

    PriceDto findByIdentifier(String identifier);

    List<PriceDto> findIfTrue();

    PriceDto toggleStatus(String identifier);
}