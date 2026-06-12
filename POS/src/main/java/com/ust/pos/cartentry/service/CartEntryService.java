package com.ust.pos.cartentry.service;

import com.ust.pos.dto.CartEntryDto;

import java.util.List;

public interface CartEntryService {

    CartEntryDto save(CartEntryDto cartEntryDto);

    void delete(String identifier);

    List<CartEntryDto> findAllEntriesForCart(String cartIdentifier);

    void deleteAllByCartIdentifier(String cartIdentifier);

}
