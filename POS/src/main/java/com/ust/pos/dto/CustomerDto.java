package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDto extends CommonDto{
    private String partyType;
    private Double balance;
    private String balanceType;
    private String username;
    private Double creditLimit;
    private String address;
    private String customerName;
    private AddressDto billingAddress;
    private AddressDto shippingAddress;
}
