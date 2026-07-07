package com.ust.pos.cartentry.service;

import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.CartEntry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface CartEntryService {
    CartEntryDto save(CartEntryDto cartEntryDto);

    CartEntryDto findByIdentifier(String identifier);

    WsDto<CartEntryDto> findAll(Pageable pageable);

    WsDto<CartEntryDto> findAll(Specification<CartEntry> example, Pageable pageable);
}