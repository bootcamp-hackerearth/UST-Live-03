package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto extends CommonDto {
    private String category;
    private String productName;
    private String brand;
    private String unit;
    private String model;
}
