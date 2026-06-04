package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductDto extends CommonDto {
    private String name;
    private String unit;
    private String brand;
    private String category;
    private String description;
    private List<String> shelf;
    private List<String> rack;
}
