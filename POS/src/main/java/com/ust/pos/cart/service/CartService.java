package com.ust.pos.cart.service;

import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

public interface CartService {
    CartDto save(CartDto cartDto);

    void delete(String identifier);

    CartDto findByIdentifier(String identifier);

    WsDto<CartDto> findAll(Pageable pageable);

    void clearCart(String cartId);
}