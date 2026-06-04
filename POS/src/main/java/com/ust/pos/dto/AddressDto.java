package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDto extends CommonDto {
    private String addressLine;
    private String city;
    private String state;
    private Long zipcode;
    private String country;
    private Long phoneNo;
    private String addressType;
}