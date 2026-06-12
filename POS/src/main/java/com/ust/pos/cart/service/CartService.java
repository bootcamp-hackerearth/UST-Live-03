package com.ust.pos.cart.service;

import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CartService {

    CartDto save(CartDto cartDto);

    void delete(String identifier);

    List<CartDto> findAll(Pageable pageable);

    CartDto findByIdentifier(String identifier);

    WsDto<CartDto> findAllws(Pageable pageable);
}