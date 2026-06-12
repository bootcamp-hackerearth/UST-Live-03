package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CartDto extends CommonDto {
    private BigDecimal totalPrice;
    private BigDecimal totalDiscount;
    private String couponCode;
    private List<CartEntryDto> cartEntryDtoList;
}
