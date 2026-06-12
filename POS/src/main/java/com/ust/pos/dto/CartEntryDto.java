package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CartEntryDto extends CommonDto{
    private String cartIdentifier;
    private String productIdentifier;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal originalPrice;
    private BigDecimal discount;
    private BigDecimal totalPrice;
}
