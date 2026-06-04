package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDto extends CommonDto {
    private String name;
    private Long phoneNo;
    private String userType;
    private Double balance;
    private Double creditLimit;
    private AddressDto shippingAddress = new AddressDto();
    private AddressDto billingAddress = new AddressDto();
    private boolean status;
}
