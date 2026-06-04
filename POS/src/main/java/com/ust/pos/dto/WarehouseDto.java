package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarehouseDto extends CommonDto {
    private String warehouseName;
    private String country;
    private String state;
    private String cityName;
    private String location;
}
