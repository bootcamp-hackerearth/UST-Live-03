package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto extends CommonDto {
    private String productName;
    private String description;
    private String category;
    private String brand;
    private String price;
    private String model;
}