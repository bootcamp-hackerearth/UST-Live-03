package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StockDto extends CommonDto {

    private List<String> warehouse;
    private Integer quantity;
    private Double unitPrice;
}
