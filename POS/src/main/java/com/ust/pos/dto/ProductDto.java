package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductDto extends CommonDto {
    private List<String> category;
    private String models;
    private String brand;
    private Double price;
    private String rack;
    private String unit;
    private List<String> shelfs;
}