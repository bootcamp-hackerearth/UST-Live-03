package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductDto extends CommonDto {

    private List<String> category;
    private String brand;
    private String unit;
    private String model;
    private String name;

}