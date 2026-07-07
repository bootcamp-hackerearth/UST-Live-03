package com.ust.pos.price.service;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface PriceService {

    PriceDto findByIdentifier(String identifier);

    PriceDto save(PriceDto dto);

    PriceDto update(PriceDto dto);

    void delete(String identifier);

    WsDto<PriceDto> findAll(Pageable pageable);

    List<PriceDto> findIfTrue();

    PriceDto toggleStatus(String identifier);

    PriceDto findByProductIdentifier(String productIdentifier);

    WsDto<PriceDto> findAll(Specification<Price> example, Pageable pageable);
}