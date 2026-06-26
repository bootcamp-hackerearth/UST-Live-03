package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockDto extends CommonDto{
    private String warehouseName;
    private Long quantity;
    private boolean status = true;
}
