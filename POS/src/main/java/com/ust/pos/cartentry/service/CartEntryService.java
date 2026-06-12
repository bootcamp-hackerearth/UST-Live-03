package com.ust.pos.cartentry.service;

import com.ust.pos.dto.CartEntryDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CartEntryService {

    CartEntryDto save(CartEntryDto dto);

    CartEntryDto update(CartEntryDto dto);

    CartEntryDto findByIdentifier(String identifier);

    List<CartEntryDto> findAll(Pageable pageable);

    List<CartEntryDto> findByCartId(String cartId);

    void delete(String identifier);
}