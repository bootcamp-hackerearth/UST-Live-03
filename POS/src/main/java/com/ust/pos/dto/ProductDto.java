package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto extends CommonDto {
    private String productName;
    private String category;
    private String brand;
    private String unit;
    private String model;
}
