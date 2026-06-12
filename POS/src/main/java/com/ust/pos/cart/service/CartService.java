package com.ust.pos.cart.service;

import com.ust.pos.dto.CartDto;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CartService {
    CartDto save(CartDto cartDto);

    CartDto update(CartDto cartDto);

    void delete(String identifier);

    void deleteAll();

    List<CartDto> findAll();

    CartDto findByIdentifier(String identifier);

    List<CartDto> findAll(Pageable pageable);

    CartDto recalulateCart(String cartId);
}