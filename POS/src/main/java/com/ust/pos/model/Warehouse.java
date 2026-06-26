package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Warehouse extends CommonFields {
    private String country;
    private Integer pincode;
    private String address;
}