package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Customer extends CommonFields {
    private String customerName;
    private String phoneNum;
    private String partyType;
    private Double balance;
    private String balanceType;
    private String username;
    private Double creditLimit;
    private String address;


}
