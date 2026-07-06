package com.ust.pos.price.service;

import com.ust.pos.dto.PaginationResponseDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.Price;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface PriceService {
    PaginationResponseDto<PriceDto> findAll(Pageable pageable);

    PaginationResponseDto<PriceDto> findAll(Specification<Price> example, Pageable pageable);

    PriceDto save(PriceDto priceDto);

    PriceDto findById(long id);

    PriceDto findByIdentifier(String identifier);

    PriceDto update(PriceDto priceDto);

    void delete(String identifier);
}
