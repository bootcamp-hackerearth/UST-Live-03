package com.ust.pos.price.service;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PriceService {
    WsDto<PriceDto> findAll(Pageable pageable);

    PriceDto save(PriceDto priceDto);

    void delete(String identifier);

    PriceDto findByIdentifier(String identifier);

    PriceDto update(PriceDto priceDto);

    PriceDto changeToggleStatus(String identifier, boolean status);

    List<PriceDto> findActiveStatus();
}
