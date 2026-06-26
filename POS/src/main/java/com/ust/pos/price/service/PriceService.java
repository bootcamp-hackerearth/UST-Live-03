package com.ust.pos.price.service;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PriceService {
    PriceDto save(PriceDto priceDto);

    WsDto<PriceDto> findAll(Pageable pageable);

    List<PriceDto> findAllActive();

    PriceDto findByIdentifier(String identifier);

    PriceDto update(PriceDto priceDto);

    PriceDto toggleStatus(String identifier);

    boolean delete(String identifier);
}
