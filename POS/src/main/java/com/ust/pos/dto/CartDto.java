package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CartDto extends CommonDto {

    private BigDecimal discount;
    private BigDecimal totalPrice;
    private BigDecimal originalPrice;
    private String coupon;
    private List<CartEntryDto> cartEntryDtoList;

}
