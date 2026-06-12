package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter

public class CartEntryDto extends CommonDto {
    private String productId;
    private String cartId;
    private BigDecimal quantity = new BigDecimal(0);
    private BigDecimal mrp;
    private BigDecimal sellingPrice;
    private BigDecimal discount;
    private BigDecimal totalPrice;
    private BigDecimal originalPrice;
}
