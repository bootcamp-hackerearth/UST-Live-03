package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDto extends CommonDto {
    private String phoneNo;
    private String addressType;
    private String city;
    private String zipCode;
    private String country;
    private String state;
    private String addressLine;
}