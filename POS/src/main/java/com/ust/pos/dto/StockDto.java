package com.ust.pos.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class StockDto extends CommonDto {

    private Long productId;
    private String productName;
    private Long warehouseId;
    private String warehouseName;
    private Integer quantity;
}