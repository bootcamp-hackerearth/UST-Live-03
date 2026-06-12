package com.ust.pos.price.service;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;


public interface PriceService {

    PriceDto save(PriceDto dto);

    WsDto<PriceDto> findAll(Pageable pageable);

    PriceDto findById(Long id);

    void delete(String identifier);

    PriceDto update(PriceDto priceDto);

    PriceDto findByIdentifier(String identifier);

    PriceDto changePriceStatus(String identifier, boolean status);
}
