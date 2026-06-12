package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CartEntryDto extends CommonDto {
    private String product;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private BigDecimal quantity = new BigDecimal(0);
    private BigDecimal discount;
    private String cartId;
    private BigDecimal totalOriginalPrice;
}
