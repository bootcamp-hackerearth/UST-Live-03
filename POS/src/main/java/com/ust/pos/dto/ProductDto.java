package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDto extends CommonDto {
    private String name;
    private Long supplierId;
    private String warehouseName;
    private String category;
}
