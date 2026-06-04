package com.ust.pos.price.service;

import com.ust.pos.dto.PriceDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PriceService {

    PriceDto createPrice(PriceDto priceDto);

    PriceDto updatePrice(PriceDto priceDto);

    List<PriceDto> findAll(Pageable pageable);

    PriceDto getPriceById(Long id);

    boolean deletePrice(Long id);
}