package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Product extends CommonFields {
    private String name;
    private String unit;
    private Double price;
    private String category;
    private String models;
    private String brand;
}
