package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Address extends CommonFields {
    private String phoneNo;
    private String addressType;
    private String city;
    private String zipCode;
    private String country;
    private String state;
    private String addressLine;
}