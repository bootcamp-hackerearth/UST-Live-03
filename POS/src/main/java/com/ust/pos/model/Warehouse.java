package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Warehouse extends CommonFields {
    private String region;
    private String city;
    private String state;
    private String country;
    private String capacity;
    private String contactName;
    private String contactNumber;
}
