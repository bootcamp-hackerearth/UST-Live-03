package com.ust.pos.modell;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Product extends CommonFields {
    @Column(nullable = false, unique = true)
    private String identifier;
    private String category;
    private String brand;
    private String model;
    private String unit;
    private Integer quantity;
    private String shelf;
    private String rack;
    private Boolean status;
}