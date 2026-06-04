package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Stock extends CommonFields {
    private String product;
    private String wareHouse;
    private Integer quantity;
    private String shelves;
    private String racks;
}