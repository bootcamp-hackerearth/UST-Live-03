package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Warehouse extends CommonFields {
    private String warehouseName;
    private String country;
    private String state;
    private String cityName;
    private String location;
}
