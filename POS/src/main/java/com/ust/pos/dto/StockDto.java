package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockDto extends CommonDto {
    private String productIdentifier;
    private String warehouseIdentifier;
    private Integer availableQuantity;
    private Integer reorderLevel;
    private String stockState;
}