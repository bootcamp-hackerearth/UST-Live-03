package com.ust.pos.cartentry.service;

import com.ust.pos.dto.CartEntryDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface CartEntryService {

    CartEntryDto save(CartEntryDto cartEntryDto);

    void delete(String identifier);

    List<CartEntryDto> findAllEntriesForCart(String cartIdentifier);

    void deleteAllByCartIdentifier(String cartIdentifier);

    void reduceQuantity(String cartIdentifier, String productIdentifier);

}
