package com.ust.pos.price.service;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface PriceService {

    PriceDto save(PriceDto priceDto);

    PriceDto update(PriceDto priceDto);

    PriceDto findByIdentifier(String identifier);

    WsDto<PriceDto> findAll(Pageable pageable);

    void delete(String identifier);

    List<String> getPriceTypes();

    List<PriceDto> findActivePrices();

    WsDto<PriceDto> findAll(Specification<Price> example, Pageable pageable);
}