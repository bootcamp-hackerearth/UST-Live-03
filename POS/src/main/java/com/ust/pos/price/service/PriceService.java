package com.ust.pos.price.service;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

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
}