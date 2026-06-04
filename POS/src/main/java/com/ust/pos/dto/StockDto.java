package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockDto extends CommonDto {
    private String stockStatus;
    private String warehouseName;
    private String productName;
    private Integer quantity;
}
