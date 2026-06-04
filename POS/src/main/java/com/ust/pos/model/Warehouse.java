package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Warehouse extends CommonFields {
    private String country;
    private String region;
    private String address;
    private String phoneNo;
}