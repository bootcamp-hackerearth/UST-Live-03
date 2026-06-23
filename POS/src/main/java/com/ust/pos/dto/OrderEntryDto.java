package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderEntryDto extends CommonDto {
    private String orderIdentifier;
    private String product;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal sellingPrice;
    private BigDecimal discount;
    private BigDecimal totalPrice;
    private String couponCode;
}