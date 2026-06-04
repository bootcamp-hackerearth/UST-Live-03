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
    private String partyType;
    private String address;
    private Double balance;
    private String creditLimit;
}
