package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto extends CommonDto {
    private String name;
    private String unit;
    private Double price;
    private String category;
    private String models;
    private String brand;
}
