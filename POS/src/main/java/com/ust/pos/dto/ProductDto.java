package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductDto extends CommonDto {
    private String productName;
    private String unit;
    private String brand;
    private String model;
    private List<String> categories;
}
