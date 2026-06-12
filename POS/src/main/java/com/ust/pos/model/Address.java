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
    private String city;
    private String state;
    private String country;
    private long zipcode;
    private String email;

}
