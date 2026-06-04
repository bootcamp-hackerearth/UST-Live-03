package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockDto extends CommonDto {
    private Long outgoingStock;
    private Long availableStock;
    private String warehouse;
    private String productStatus;
}
