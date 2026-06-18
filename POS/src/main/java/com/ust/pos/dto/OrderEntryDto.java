package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderEntryDto extends CommonDto {

    private String orderIdentifier;
    private String productIdentifier;
    private ProductDto product;
    private Integer quantity;
    private BigDecimal sellingPrice;
    private BigDecimal mrpPrice;
    private BigDecimal totalPrice;
    private BigDecimal discount;
}