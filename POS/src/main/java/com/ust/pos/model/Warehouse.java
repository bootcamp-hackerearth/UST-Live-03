package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Warehouse extends CommonFields {
    private String region;
    private String country;
    private String location;
    private String contactName;
    private String contactNumber;
}
