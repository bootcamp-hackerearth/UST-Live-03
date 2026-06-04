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
    private String brand;
    private String model;
    private Double price;
    private String category;


}
