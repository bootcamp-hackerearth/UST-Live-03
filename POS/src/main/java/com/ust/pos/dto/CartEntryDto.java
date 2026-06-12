package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CartEntryDto extends CommonDto {

    private String cartIdentifier;
    private String productIdentifier;
    private BigDecimal totalPrice;
    private BigDecimal discount;
    private Integer quantity;
    private ProductDto product;
    private BigDecimal mrpPrice;
    private BigDecimal sellingPrice;
}