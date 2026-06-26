package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderEntryDto extends CommonDto {
    private String orderIdentifier;
    private String productIdentifier;
    private BigDecimal originalPrice;
    private BigDecimal discount;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal totalPrice;
}