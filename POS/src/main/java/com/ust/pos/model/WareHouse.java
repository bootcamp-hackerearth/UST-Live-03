package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class WareHouse extends CommonFields {
    private String location;
    private String contactPerson;
    private String phoneNumber;
}