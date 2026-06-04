package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CustomerDto extends CommonDto {
    private String name;
    private String email;
    private String phoneNo;
    private Double balance;
    private String balanceType;
    private String partyType;
    private Double creditLimit;
    private AddressDto billingAddress;
    private AddressDto shippingAddress;
}
