package com.ust.pos.cart.service;

import com.ust.pos.dto.CartDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CartService {
    CartDto save(CartDto cartDto);

    List<CartDto> findAll(Pageable pageable);

    CartDto findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    CartDto update(CartDto cartDto);

    CartDto recalculate(String identifier);
}