package com.ust.pos.cart.service;

import com.ust.pos.dto.CartDto;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface CartService {
    CartDto save(CartDto cartDto);

    CartDto findByIdentifier(String identifier);

    CartDto recalculate(String cart);

    boolean delete(String identifier);
}