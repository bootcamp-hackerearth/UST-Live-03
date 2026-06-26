package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderEntryDto extends CommonDto {

    private String product;

    private BigDecimal discount;
    private BigDecimal totalPrice;
    private BigDecimal totalOriginalPrice;

    private String orderId;

    private BigDecimal quantity;
    private BigDecimal unitPrice;
}
