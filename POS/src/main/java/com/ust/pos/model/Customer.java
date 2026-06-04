package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Customer extends CommonFields {
    private String customerName;
    private String email;
    private String partyType;
    private Double credit;
    private Double creditLimit;
    private String creditType;
}