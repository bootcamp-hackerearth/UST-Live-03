package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WarehouseDto extends CommonDto {
    private String country;
    private Integer pincode;
    private String address;
}