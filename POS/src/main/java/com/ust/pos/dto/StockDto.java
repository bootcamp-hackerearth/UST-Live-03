package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StockDto extends CommonDto {
    private String product;
    private long quantity;
    private long reorderLevel;
    private String warehouse;
    private String rack;
    private String shelf;
}
