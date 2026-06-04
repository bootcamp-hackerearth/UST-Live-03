package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Address extends CommonFields {

    private String addressLine;
    private String city;
    private String state;
    private String country;
    private String addressType;
    private Long zipcode;
    private Long phoneNumber;

}
