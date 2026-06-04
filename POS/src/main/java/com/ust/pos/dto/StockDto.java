package com.ust.pos.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockDto extends CommonDto {
    private Long productId;
    private Long warehouseId;
    private String productIdentifier;
    private String warehouseIdentifier;
    private Integer quantity;
    private Integer minimumStock;
    private boolean status;
}
