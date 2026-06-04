package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDto extends CommonDto {
    private String customerName;
    private String partyType;
    private Double balance;
    private String balanceType;
    private String username;
    private Double creditLimit;
    private String address;
    private AddressDto billingAddress;
    private AddressDto shippingAddress;
}
