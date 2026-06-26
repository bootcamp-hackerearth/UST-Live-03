package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ProductDto extends CommonDto {
    private List<String> categories;
    private String supplierID;
    private String brand;
    private String unit;
}