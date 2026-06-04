package com.ust.pos.modell;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Customer extends CommonFields {
    private String customerName;
    @Column(name = "phone_no", unique = true, nullable = false)
    private String phoneNo;
    private String partyType;
    private String creditType;
    private Double credit;
    private Double creditLimit;
    private boolean status;
}