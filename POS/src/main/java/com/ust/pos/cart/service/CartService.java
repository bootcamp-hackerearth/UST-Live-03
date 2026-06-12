package com.ust.pos.cart.service;

import com.ust.pos.dto.CartDto;


public interface CartService {

    CartDto save(CartDto cartDto);

    void deleteByIdentifier(String identifier);

    CartDto recalculate(String cart);

    CartDto findByIdentifier(String identifier);

}