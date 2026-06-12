package com.ust.pos.cartentry.service;

import com.ust.pos.dto.CartEntryDto;

import java.util.List;

public interface CartEntryService {
    CartEntryDto save(CartEntryDto cartEntryDto);

    List<CartEntryDto> findAllEntriesForCart(String cart);

    void deleteByIdentifier(String identifier);

    void deleteAllByCart(String cart);
}
