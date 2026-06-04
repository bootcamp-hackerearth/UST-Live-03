package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Customer extends CommonFields {

    private String name;
    private long phoneNo;
    private List<String> partyType;
    private Double balance;
    private Double creditLimit;
}
