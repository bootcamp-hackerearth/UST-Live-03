package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CartDto extends CommonDto {
    private BigDecimal originalPrice = new BigDecimal(0);
    private BigDecimal totalPrice = new BigDecimal(0);
    private BigDecimal discount = new BigDecimal(0);
    private String coupon;
}