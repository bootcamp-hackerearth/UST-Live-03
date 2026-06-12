package com.ust.pos.cartentry.service;

import com.ust.pos.dto.CartEntryDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CartEntryService {
    List<CartEntryDto> findAll(Pageable pageable);

    List<CartEntryDto> findByCart(String cart);

    CartEntryDto findByIdentifier(String identifier);

    CartEntryDto save(CartEntryDto cartEntryDto);

    CartEntryDto update(CartEntryDto cartEntryDto);

    void delete(String identifier);
}
