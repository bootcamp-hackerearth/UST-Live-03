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
    private List<String> categories;
    private String brand;
    private String model;
    private List<String> shelf;
    private String unit;
    private boolean status;

}
