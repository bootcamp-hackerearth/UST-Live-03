package com.ust.pos.cart.service;


import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

public interface CartService {

    CartDto save(CartDto cartDto);

    WsDto<CartDto> findAll(Pageable pageable);

    CartDto findByIdentifier(String identifier);

    void deleteByIdentifier(String identifier);

    CartDto recalculate(String identifier);

}