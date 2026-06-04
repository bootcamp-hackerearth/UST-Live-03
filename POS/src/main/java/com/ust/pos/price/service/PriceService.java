package com.ust.pos.price.service;

import com.ust.pos.dto.PriceDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PriceService {
    PriceDto save(PriceDto productDto);

    PriceDto update(PriceDto productDto);

    void delete(String identifier);

    List<PriceDto> findAll();

    PriceDto findByIdentifier(String identifier);

    List<PriceDto> findAll(Pageable pageable);
}
