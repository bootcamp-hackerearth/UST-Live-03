package com.ust.pos.model;


import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Product extends CommonFields {
    private String name;
    private String unit;
    private String brand;
    private String category;
    private String description;
    private List<String> shelf;
    private List<String> rack;
}
