package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CartEntryDto extends CommonDto {
    private String product;
    private String cart;
    private BigDecimal discount;
    private BigDecimal unitPrice;
    private BigDecimal quantity = new BigDecimal(0);
    private BigDecimal originalPrice;
    private BigDecimal totalPrice;
}
