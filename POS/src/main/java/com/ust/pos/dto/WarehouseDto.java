package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarehouseDto extends CommonDto {
    private String region;
    private String country;
    private String location;
    private String contactName;
    private String contactNumber;
}