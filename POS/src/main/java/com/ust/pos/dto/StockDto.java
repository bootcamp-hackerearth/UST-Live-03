package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockDto extends CommonDto {

    private String stockStatus;
    private String warehouse;
    private long quantity;
    private int minimumStock;
    private String product;

}
