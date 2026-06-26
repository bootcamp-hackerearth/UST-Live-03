package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrderItemDto extends CommonDto {

    private String orderIdentifier;

    private String product;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalPrice;
}