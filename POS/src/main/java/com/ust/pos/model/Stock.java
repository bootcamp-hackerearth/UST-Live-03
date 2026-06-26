package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Stock extends CommonFields {
    private String product;
    private String wareHouse;
    private Integer quantity;
    private List<String> shelves;
    private String racks;
}