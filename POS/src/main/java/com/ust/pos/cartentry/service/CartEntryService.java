package com.ust.pos.cartentry.service;

import com.ust.pos.dto.CartEntryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CartEntryService {
    CartEntryDto save(CartEntryDto cartEntryDto);

    CartEntryDto update(CartEntryDto cartEntryDto);

    void delete(String identifier);

    List<CartEntryDto> findAll();

    CartEntryDto findByIdentifier(String identifier);

    Page<CartEntryDto> findAll(Pageable pageable, String search);

    List<CartEntryDto> findByCartId(String cart);

    CartEntryDto decreaseQuantity(String identifier);
}
