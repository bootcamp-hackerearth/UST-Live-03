package com.ust.pos.price.service;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;


public interface PriceService {
    WsDto<PriceDto> findAll(Pageable pageable);

    PriceDto findByIdentifier(String priceCode);

    PriceDto save(PriceDto priceDto);

    PriceDto update(PriceDto priceDto);

    String delete(String identifier);
}
