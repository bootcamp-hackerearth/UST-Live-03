package com.ust.pos.price.service;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface PriceService {

    PriceDto createPrice(PriceDto priceDto);

    PriceDto updatePrice(PriceDto priceDto);

    WsDto<PriceDto> findAll(Pageable pageable);

    PriceDto getPriceById(Long id);

    boolean deletePrice(Long id);

    WsDto<PriceDto> findAll(Specification<Price> spec, Pageable pageable, String keyword);
}