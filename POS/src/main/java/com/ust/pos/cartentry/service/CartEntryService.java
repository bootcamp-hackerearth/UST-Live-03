package com.ust.pos.cartentry.service;

import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.PaginatedResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CartEntryService {

    CartEntryDto save(CartEntryDto cartEntryDto);

    void delete(String identifier);

    PaginatedResponseDto<CartEntryDto> findAll(Pageable pageable);

    CartEntryDto findByIdentifier(String identifier);

    List<CartEntryDto> findByCartId(String cartId);
}
