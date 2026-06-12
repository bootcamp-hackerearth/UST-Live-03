package com.ust.pos.price.service;
import com.ust.pos.dto.PageDto;
import com.ust.pos.dto.PriceDto;
import org.springframework.data.domain.Pageable;

public interface PriceService {
    PriceDto save(PriceDto priceDto);

    PriceDto update(PriceDto priceDto);

    boolean delete(String identifier);

    PageDto<PriceDto> findAll(Pageable pageable);

    PriceDto  findByIdentifier(String identifier);
}
