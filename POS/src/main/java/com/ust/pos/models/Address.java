package com.ust.pos.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Address extends CommonFields {
    private String addressType;
    private String addressLine;
    private String phoneNo;
    private String city;
    private String state;
    private String zipCode;
    private String country;

}