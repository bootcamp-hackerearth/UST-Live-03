package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderEntryDto extends CommonDto {
    private String orderId;
    private String product;
    private Integer quantity;
    private Double unitPrice;
    private Double discount;
    private Double totalPrice;
}