package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductDto extends CommonDto {

    private String name;
    private List<String> categories;
    private String brand;
    private String model;
    private List<String> shelf;
    private String unit;
    private boolean status;

}
