package com.ust.pos.price.service;

import com.ust.pos.dto.PriceDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.model.Price;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PriceService {
    PriceDto save(PriceDto priceDto);

    PriceDto update(PriceDto priceDto);

    void delete(String identifier);

    List<PriceDto> findAll();

    WsDto<PriceDto> findAll(Pageable pageable);

    Page<PriceDto> findAll(Example<Price> example, Pageable pageable);

    PriceDto findByIdentifier(String identifier);
}
