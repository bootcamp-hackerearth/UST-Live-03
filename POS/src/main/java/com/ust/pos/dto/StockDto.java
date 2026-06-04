package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockDto extends CommonDto {
    private String productname;
    private String warehouse;
    private int quantity;
    private double price;

}
