package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter

public class CartDto extends CommonDto {
    private BigDecimal originalPrice;
    private BigDecimal totalPrice;
    private BigDecimal discount;
    private String coupon;
}
