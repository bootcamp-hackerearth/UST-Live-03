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
    private BigDecimal originalPrice;
    private String cartId;
    private BigDecimal discount;
    private BigDecimal quantity;
}
