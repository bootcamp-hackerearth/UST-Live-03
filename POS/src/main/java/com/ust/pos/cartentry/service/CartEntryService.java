package com.ust.pos.cartentry.service;

import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

public interface CartEntryService {
    CartEntryDto save(CartEntryDto cartEntryDto);

    CartEntryDto findByIdentifier(String identifier);

    WsDto<CartEntryDto> findAll(Pageable pageable);

}