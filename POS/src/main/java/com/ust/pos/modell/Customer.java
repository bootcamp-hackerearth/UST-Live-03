package com.ust.pos.modell;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity

public class Customer extends CommonFields {
    private String customerName;
    private String phoneNo;
    private String partyType;
    private String creditType;
    private Double credit;
    private Double creditLimit;
}