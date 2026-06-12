package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Customer extends CommonFields {
    private String name;
    private Long phoneNo;
    private String userType;
    private Double balance;
    private Double creditLimit;
    private boolean status;

}
