package com.ust.pos.price.service;

import com.ust.pos.dto.PaginatedResponseDto;
import com.ust.pos.dto.PriceDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PriceService {

    PriceDto save(PriceDto priceDto);

    PriceDto update(PriceDto priceDto);

    void delete(String identifier);

    PaginatedResponseDto<PriceDto> findAll(Pageable pageable);

    PriceDto findByIdentifier(String identifier);

    List<PriceDto> findAllActive();

    void changeStatus(String identifier, boolean status);

}

