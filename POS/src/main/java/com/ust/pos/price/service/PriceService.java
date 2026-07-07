package com.ust.pos.price.service;

import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.Price;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface PriceService {

    PriceDto save(PriceDto priceDto);

    PriceDto update(PriceDto priceDto);

    PriceDto delete(String identifier);

    PaginatedResponseDto<PriceDto> findAll(Pageable pageable);

    PriceDto findByIdentifier(String identifier);

    List<PriceDto> findAllActive();

    void changeStatus(String identifier, boolean status);

    PaginatedResponseDto<PriceDto> findAll(Specification<Price> example, Pageable pageable);
}