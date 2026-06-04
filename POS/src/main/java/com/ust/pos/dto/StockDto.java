package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockDto extends CommonDto {

    private Integer quantity;
    private String stockStatus;
    private String warehouse;
    private String unit;


}
