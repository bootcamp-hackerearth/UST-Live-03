package com.ust.pos.price.service;

import com.ust.pos.dto.PriceDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PriceService {

    PriceDto save(PriceDto dto);

    List<PriceDto> findAll(Pageable pageable);

    PriceDto findById(Long id);

    void delete(String identifier);

    PriceDto update(PriceDto priceDto);

    PriceDto findByIdentifier(String identifier);
}
