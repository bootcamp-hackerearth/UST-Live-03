package com.ust.pos.price.service;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.PriceDto;
import com.ust.pos.model.Price;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface PriceService {
    PriceDto save(PriceDto priceDto);

    PriceDto update(PriceDto priceDto);

    boolean delete(String identifier);

    PageDto<PriceDto> findAll(Pageable pageable);

    PriceDto  findByIdentifier(String identifier);

    void toggleStatus(String identifier);

    List<PriceDto> findActivePrices();

    PageDto<PriceDto> findAll(Specification<Price> spec, Pageable pageable, String keyword);
}
