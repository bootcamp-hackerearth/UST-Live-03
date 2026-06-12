package com.ust.pos.price.service;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

public interface PriceService {

    PriceDto save(PriceDto priceDto);

    PriceDto update(PriceDto priceDto);

    PriceDto findByIdentifier(String identifier);

    WsDto<PriceDto> findAll(Pageable pageable);

    void delete(String identifier);
}