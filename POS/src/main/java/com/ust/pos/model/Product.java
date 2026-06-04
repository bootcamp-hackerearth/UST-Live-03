package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Product extends CommonFields {

    private List<String> category;
    private String productName;
    private List<String> brand;
    private List<String> unit;
    private List<String> model;
}