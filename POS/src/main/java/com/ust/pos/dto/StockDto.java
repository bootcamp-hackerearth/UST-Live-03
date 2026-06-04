package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockDto extends CommonDto {
    private Integer minimumstock;
    private Integer quantity;
    private String warehouse;
    private String product;
}