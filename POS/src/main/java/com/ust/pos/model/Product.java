package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Product extends CommonFields {
    private String warehouseName;
    private long supplierId;
    private String category;
}
