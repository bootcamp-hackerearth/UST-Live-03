package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CartEntryDto extends CommonDto {
    private String product;
    private BigDecimal discount = new BigDecimal(0);
    private BigDecimal totalPrice = new BigDecimal(0);
    private BigDecimal unitPrice = new BigDecimal(0);
    private String cartId;
    private BigDecimal quantity = new BigDecimal(0);
}
