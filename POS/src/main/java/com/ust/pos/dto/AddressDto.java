package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDto extends CommonDto {
    private String addressType;
    private String phoneNo;
    private String addressLine;
    private String city;
    private String state;
    private String country;
    private String zipcode;
    private String customerName;
}
