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
    private String brandName;
    private String model;
    private List<String> category;
    private String unit;
}
