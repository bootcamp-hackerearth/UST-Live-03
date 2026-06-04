package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class WareHouse extends CommonFields {

    private String location;
    private String manager;

}
