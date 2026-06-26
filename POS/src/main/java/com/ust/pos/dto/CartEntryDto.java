package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class CartEntryDto extends CommonDto{
    private String product;
    private String cartId;
    private BigDecimal discount;
    private BigDecimal totalPrice;
    private BigDecimal unitPrice;
    private BigDecimal quantity = new BigDecimal(0);
}
