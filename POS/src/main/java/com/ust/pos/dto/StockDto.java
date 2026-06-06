package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockDto extends CommonDto {
    private String productName;
    private Integer quantity;
    private String warehouseName;
    private String stockStatus;
}
