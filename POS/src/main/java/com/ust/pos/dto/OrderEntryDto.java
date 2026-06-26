package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class OrderEntryDto extends CommonDto {

    private String product;
    private BigDecimal discount;
    private BigDecimal totalPrice;
    private BigDecimal unitPrice;
    private BigDecimal quantity;
    private BigDecimal totalOriginalPrice;
    private String orderId;
}
