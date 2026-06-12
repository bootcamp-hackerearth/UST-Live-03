package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WarehouseDto extends CommonDto {
    private String country;
    private int pincode;
    private String address;
}
