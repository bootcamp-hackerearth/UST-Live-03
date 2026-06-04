package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDto extends CommonDto {
    private String customerName;
    private String partyType;
    private String phoneNo;
    private Long balance;
    private Long creditLimit;
    private AddressDto billingAddress;
    private AddressDto shippingAddress;
}
