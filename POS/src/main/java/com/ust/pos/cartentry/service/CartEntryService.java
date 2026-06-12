package com.ust.pos.cartentry.service;

import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CartEntryService {

    CartEntryDto save(CartEntryDto cartEntryDto);

    WsDto<CartEntryDto> findAll(Pageable pageable);

    CartEntryDto findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    List<CartEntryDto> findByCart(String cartIdentifier);

}