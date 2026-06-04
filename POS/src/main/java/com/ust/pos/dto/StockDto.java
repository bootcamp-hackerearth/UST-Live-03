package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockDto extends CommonDto {
    private Integer quantity;
    private Integer minimumStock;
    private String productIdentifier;
    private String warehouseIdentifier;


}
