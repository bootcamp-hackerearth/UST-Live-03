package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderEntryDto extends CommonDto {

    private String orderId;

    private String product;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private BigDecimal discount;

    private BigDecimal totalPrice;
}