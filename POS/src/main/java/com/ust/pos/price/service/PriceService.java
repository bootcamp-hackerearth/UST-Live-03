package com.ust.pos.price.service;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

public interface PriceService {

    PriceDto createPrice(PriceDto priceDto);

    PriceDto updatePrice(PriceDto priceDto);

    WsDto<PriceDto> findAll(Pageable pageable);

    PriceDto getPriceById(Long id);

    boolean deletePrice(Long id);
}