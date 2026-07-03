package com.ust.pos.price.service;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface PriceService {
    WsDto<PriceDto> findAll(Pageable pageable);

    WsDto<PriceDto> findAll(Specification<Price>example,Pageable pageable);

    PriceDto save(PriceDto priceDto);

    void delete(String identifier);

    PriceDto findByIdentifier(String identifier);

    PriceDto update(PriceDto priceDto);

    PriceDto changeToggleStatus(String identifier, boolean status);

    List<PriceDto> findActiveStatus();
}
