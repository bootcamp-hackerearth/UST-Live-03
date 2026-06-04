package com.ust.pos.model;


import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Stock extends CommonFields {
    private String productname;
    private String warehouse;
    private int quantity;
    private double price;
}
