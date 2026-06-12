package com.ust.pos.cart.service;

import com.ust.pos.dto.CartDto;

public interface CartService {

    CartDto findByIdentifier(String identifier);

    CartDto save(CartDto cartDto);

    CartDto recalculate(String cart);

    void deleteByIdentifier(String identifier);
}