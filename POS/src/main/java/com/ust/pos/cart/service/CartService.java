package com.ust.pos.cart.service;

import com.ust.pos.dto.CartDto;
import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {
    WsDto<CartDto> findAll(Pageable pageable);

    CartDto findByIdentifier(String identifier);

    List<CartEntryDto> getAllCartEntriesByCartId(String cartId);

    BigDecimal findTotalPrice(List<CartEntryDto> cartEntryDtoList);

    BigDecimal getDiscount(List<CartEntryDto> cartEntryDtoList);

    CartDto save(CartDto cartDto);

    void delete(String identifier);
}
