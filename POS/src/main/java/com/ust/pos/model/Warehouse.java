package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Warehouse extends CommonFields {

    private String region;
    private String country;
    private String contactName;
    private String contactNumber;
    private String location;
}
