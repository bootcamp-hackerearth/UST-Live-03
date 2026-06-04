package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto extends CommonDto {
    private String name;
    private String unit;
    private String brand;
    private String model;
    private double price;
    private String category;
}
