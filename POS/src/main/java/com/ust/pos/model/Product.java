package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Product extends CommonFields {
    private String productName;
    private String unit;
    private String brand;
    private String model;
    private List<String> categories;
}