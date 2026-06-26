package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CartEntryDto extends CommonDto{

    private BigDecimal price;
    private BigDecimal totalPrice;
    private BigDecimal sellingPrice;
    private BigDecimal discount;
    private BigDecimal quantity;
    private String couponCode;
    private String product;
    private String cart;

}