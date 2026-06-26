package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Address extends CommonFields{
    private String addressLine;
    private String city;
    private String state;
    private Long pincode;
    private String country;
    private Boolean isShipping;
    private Boolean isBilling;
}
