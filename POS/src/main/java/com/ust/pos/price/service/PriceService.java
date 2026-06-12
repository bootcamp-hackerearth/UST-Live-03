package com.ust.pos.price.service;

import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.PriceDto;
import org.springframework.data.domain.Pageable;

public interface PriceService {
    PaginationResponseDto<PriceDto> findAll(Pageable pageable);

    PriceDto save(PriceDto priceDto);

    PriceDto findById(long id);

    PriceDto findByIdentifier(String identifier);

    PriceDto update(PriceDto priceDto);

    void delete(String identifier);
}
