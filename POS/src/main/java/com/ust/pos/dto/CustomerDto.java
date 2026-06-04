package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDto extends CommonDto {

    private String customerName;
    private String email;
    private String partyType;
    private String creditType;
    private Double credit;
    private Double creditLimit;
    private AddressDto billingAddress;
    private AddressDto shippingAddress;
}
