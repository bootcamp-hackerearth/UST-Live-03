package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Product extends CommonFields {

    private List<String> category;
    private String brand;
    private String unit;
    private String model;
}
