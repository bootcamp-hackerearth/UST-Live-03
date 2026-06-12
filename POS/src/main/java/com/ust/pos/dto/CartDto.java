package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CartDto extends CommonDto{
    private BigDecimal totalPrice;
    private String couponCode;
    private BigDecimal totalDiscount;
    private List<CartEntryDto> entryDtoList;

}
