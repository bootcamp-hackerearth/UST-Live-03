package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockDto extends CommonDto {
    private String stockStatus;
    private String productName;
    private Integer noOfProducts;
    private String warehouseName;
}
