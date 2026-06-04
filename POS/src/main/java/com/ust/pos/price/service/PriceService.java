package com.ust.pos.price.service;

import com.ust.pos.dto.PriceDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PriceService {
    List<PriceDto> findAll(Pageable pageable);

    PriceDto findByIdentifier(String priceCode);

    PriceDto save(PriceDto priceDto);

    PriceDto update(PriceDto priceDto);

    String delete(String identifier);
}
