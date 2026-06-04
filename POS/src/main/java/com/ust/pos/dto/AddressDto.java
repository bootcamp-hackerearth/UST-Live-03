package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDto extends CommonDto {

    private String addressLine;
    private String city;
    private String state;
    private String country;
    private String addressType;
    private Long zipcode;
    private Long phoneNumber;

}
