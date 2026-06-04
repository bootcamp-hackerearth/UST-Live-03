package com.ust.pos.price.service;

import com.ust.pos.dto.PriceDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PriceService {
    List<PriceDto> findAll(Pageable pageable);

    PriceDto save(PriceDto priceDto);

    PriceDto findByIdentifier(String identifier);

    PriceDto update(PriceDto priceDto);

    void delete(String identifier);
}
