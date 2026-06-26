package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OrdersEntryDto extends CommonDto {

    private String ordersId;

    private String productId;

    private String productName;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private BigDecimal discount;

    private BigDecimal totalPrice;
}