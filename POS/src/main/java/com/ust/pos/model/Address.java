package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Address extends CommonFields {
    private String addressType;
    private String addressLine;
    private String phoneNo;
    private String city;
    private String state;
    private String zipCode;
    private String country;

}