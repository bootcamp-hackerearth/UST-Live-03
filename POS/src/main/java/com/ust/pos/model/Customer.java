package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Customer extends CommonFields{
    private String partyType;
    private Double balance;
    private String balanceType;
    private String username;
    private String customerName;
    private Double creditLimit;

}
