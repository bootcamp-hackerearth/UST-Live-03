package com.ust.pos.dto;

import com.ust.pos.model.Address;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerDto extends CommonDto {
    private String name;
    private String email;
    private String partyType;
    private String address;
    private Double balance;
    private String creditLimit;
    private Address shippingAddress;
    private Address billingAddress;
}
