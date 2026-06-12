package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Stock extends CommonFields {
    private Integer quantity;
    private Integer minimumStock;
    private String productIdentifier;
    private String warehouseIdentifier;

}
