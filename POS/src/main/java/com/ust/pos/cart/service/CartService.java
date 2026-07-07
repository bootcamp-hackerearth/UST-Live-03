package com.ust.pos.cart.service;

import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.WsDto;
import com.ust.pos.modell.Cart;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface CartService {
    CartDto save(CartDto cartDto);

    void delete(String identifier);

    CartDto findByIdentifier(String identifier);

    WsDto<CartDto> findAll(Pageable pageable);

    void clearCart(String cartId);

    WsDto<CartDto> findAll(Specification<Cart> example, Pageable pageable);
}