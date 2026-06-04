package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarehouseDto extends CommonDto {

    private String name;
    private String country;
    private String region;
    private String address;
    private long phoneNo;
    
}
