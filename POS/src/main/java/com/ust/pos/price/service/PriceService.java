package com.ust.pos.price.service;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface PriceService {

    PriceDto save(PriceDto priceDto);

    WsDto<PriceDto> findAll(Pageable pageable);

    PriceDto findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    PriceDto update(PriceDto priceDto);

}