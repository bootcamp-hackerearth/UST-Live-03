package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDto extends CommonDto {
    private String addressType;
    private String addressLine;
    private String city;
    private String state;
    private String country;
    private long zipcode;
    private String email;
}
