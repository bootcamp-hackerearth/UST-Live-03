package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarehouseDto extends CommonDto {

    private String name;
    private long phoneNo;
    private String country;
    private String address;
    private String region;
}



