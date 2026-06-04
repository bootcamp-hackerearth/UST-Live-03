package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Customer extends CommonFields {
    private String name;
    private String phoneNo;
    private String partyType;
    private double balance;
    private String balanceType;
    private String email;
    private long creditLimit;
    private String billingAddress;
    private String shippingAddress;
}