package com.ust.pos.price.service;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Price;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface PriceService {
    PriceDto findByIdentifier(String identifier);

    PriceDto save(PriceDto priceDto);

    PriceDto update(PriceDto priceDto);

    void delete(String identifier);

    WsDto<PriceDto> findAll(Pageable pageable);

    WsDto<PriceDto> findAll(Specification<Price> example, Pageable pageable);
}