package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductDto extends CommonDto {

    private List<String> category;
    private String productName;
    private List<String> brand;
    private List<String> unit;
    private List<String> model;
    private PriceDto price;
}