package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Warehouse extends CommonFields {
    private String name;
    private long phoneNo;
    private String country;
    private String address;
    private String region;
}
