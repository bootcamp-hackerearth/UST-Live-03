package com.ust.pos.cartentry.service;

import com.ust.pos.dto.CartEntryDto;

import java.util.List;

public interface CartEntryService {

    CartEntryDto save(CartEntryDto cartentryDto);

    void deleteAllByCart(String cart);

    void deleteByIdentifier(String identifier);

    List<CartEntryDto> findAllCarts(String cart);
}