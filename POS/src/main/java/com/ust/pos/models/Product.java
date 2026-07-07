package com.ust.pos.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Product extends CommonFields {
    private String category;
    private String brand;
    private String model;
    private String unit;
    private Integer quantity;
}