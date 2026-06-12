package com.ust.pos.cart.service;

import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.PaginatedResponseDto;
import org.springframework.data.domain.Pageable;

public interface CartService {

    CartDto save(CartDto cartDto);

    void delete(String identifier);

    PaginatedResponseDto<CartDto> findAll(Pageable pageable);

    CartDto findByIdentifier(String identifier);

    void recalculate(String cartId);
}
