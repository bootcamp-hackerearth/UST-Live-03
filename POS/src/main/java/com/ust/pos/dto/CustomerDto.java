package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDto extends CommonDto {
    private String name;
    private String phoneNo;
    private String partyType;
    private double balance;
    private String balanceType;
    private String email;
    private long creditLimit;
    private String address;
    private AddressDto billingAddress;
    private AddressDto shippingAddress;
}
