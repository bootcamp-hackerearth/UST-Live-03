package com.ust.pos.price.service;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;


public interface PriceService {

    PriceDto save(PriceDto dto);

    WsDto<PriceDto> findAll(Pageable pageable);

    WsDto<PriceDto> findAll(Specification<Price> spec, Pageable pageable);

    PriceDto findById(Long id);

    void delete(String identifier);

    PriceDto update(PriceDto priceDto);

    PriceDto findByIdentifier(String identifier);

    PriceDto changePriceStatus(String identifier, boolean status);
}