package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AddressDto extends CommonDto {
    private String addressType;
    private String addressLine;
    private String phoneNo;
    private String city;
    private String state;
    private String zipCode;
    private String country;
}