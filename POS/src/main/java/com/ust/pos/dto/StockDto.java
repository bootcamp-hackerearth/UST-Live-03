package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StockDto extends CommonDto {
    private String product;
    private String wareHouse;
    private Integer quantity;
    private List<String> shelves;
    private String racks;
}