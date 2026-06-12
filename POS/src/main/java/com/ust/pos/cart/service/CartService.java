package com.ust.pos.cart.service;

import com.ust.pos.dto.CartDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CartService {

    CartDto findByIdentifier(String identifier);

    CartDto save(CartDto cartDto);

    CartDto update(CartDto cartDto);

    boolean delete(String identifier);

    boolean deleteCartEntry(String identifier);

    List<CartDto> findAll(Pageable pageable);
}