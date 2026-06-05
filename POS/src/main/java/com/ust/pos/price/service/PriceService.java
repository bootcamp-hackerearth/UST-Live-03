package com.ust.pos.price.service;

import com.ust.pos.dto.PriceDto;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PriceService {
    PriceDto save(PriceDto priceDto);

    List<PriceDto> findAll(Pageable pageable);

    PriceDto findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    PriceDto update(PriceDto priceDto);
}