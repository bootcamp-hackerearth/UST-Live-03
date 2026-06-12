package com.ust.pos.cartentry.service;

import com.ust.pos.dto.CartEntryDto;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface CartEntryService {
    CartEntryDto save(CartEntryDto cartEntryDto);

    List<CartEntryDto> findAll(Pageable pageable);

    CartEntryDto findByIdentifier(String identifier);

    List<CartEntryDto> findAllCarts(String cart);

    boolean delete(String product, String cart);

    boolean deleteAllByCart(String cart);
}