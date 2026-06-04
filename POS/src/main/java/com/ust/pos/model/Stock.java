package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Stock extends CommonFields {
    private String productName;
    private String warehouseName;
    private String stockStatus;
    private Integer noOfProducts;
}