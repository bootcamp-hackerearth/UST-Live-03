package com.ust.pos.cart.service;

import com.ust.pos.dto.CartDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CartService {

    List<CartDto> findAll(Pageable pageable);

    CartDto findByIdentifier(String identifier);

    CartDto save(String identifier);

    void delete(String identifier);

    CartDto recalculate(String identifier);
}