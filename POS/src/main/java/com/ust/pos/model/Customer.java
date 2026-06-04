package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Customer extends CommonFields {
    private String name;
    private String email;
    private String phoneNo;
    private double balance;
    private String balanceType;
    private String partyType;
    private long creditLimit;
    private String address;
}