package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class ProductDto extends CommonDto {
    private String name;
    private String brandName;
    private String model;
    private List<String> category;
    private String unit;
    private BigDecimal sellingPrice;
    private BigDecimal mrp;
    private long stockQuantity;
}
