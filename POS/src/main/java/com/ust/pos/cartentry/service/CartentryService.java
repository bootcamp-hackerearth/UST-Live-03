package com.ust.pos.cartentry.service;

import com.ust.pos.dto.CartEntryDto;
import com.ust.pos.dto.WsDto;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface CartentryService {
    WsDto<CartEntryDto> findAll(Pageable pageable);

    CartEntryDto findByIdentifier(String cartEntryCode);

    CartEntryDto save(CartEntryDto cartEntryDto);

    void recalculate(String cartId);

    void delete(String identifier, String cartId);

    void deleteAllByCartId(String cartId);

    BigDecimal getMrpPrice(String product);

    BigDecimal getSellingPrice(String product);

    BigDecimal getDiscount(CartEntryDto cartEntryDto);

    BigDecimal getTotalPrice(String product, BigDecimal quantity);

    CartEntryDto updateQuantity(CartEntryDto cartEntryDto);

    List<CartEntryDto> findByCartId(String cartId);
}
