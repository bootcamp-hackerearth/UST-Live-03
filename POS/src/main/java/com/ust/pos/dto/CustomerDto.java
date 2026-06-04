package com.ust.pos.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDto extends CommonDto{
    private String customerName;
    @Column(name = "phone_no", unique = true, nullable = false)
    private String phoneNo;
    private String partyType;
    private String creditType;
    private Double credit;
    private Double creditLimit;
    private AddressDto billingAddress;
    private AddressDto shippingAddress;
    private boolean status;
}