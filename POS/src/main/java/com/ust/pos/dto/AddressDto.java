package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddressDto extends CommonDto {

    private String addressLine;
    private String city;
    private String state;
    private Long zipcode;
    private String country;
    private Long phoneNo;
    private String addressType;
}
