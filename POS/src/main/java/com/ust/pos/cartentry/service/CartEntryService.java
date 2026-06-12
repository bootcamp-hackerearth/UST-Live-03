package com.ust.pos.cartentry.service;

import com.ust.pos.dto.CartEntryDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CartEntryService {

    CartEntryDto findByIdentifier(String identifier);

    CartEntryDto save(CartEntryDto cartEntryDto);

    CartEntryDto update(CartEntryDto cartEntryDto);

    boolean delete(String identifier);

    boolean deleteByCartIdentifier(String cartIdentifier);

    List<CartEntryDto> findAll(Pageable pageable);

    List<CartEntryDto> findAllByCartIdentifier(String cartIdentifier);
}