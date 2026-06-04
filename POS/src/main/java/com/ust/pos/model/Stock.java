package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Stock extends CommonFields {

    private Integer minimumStock;
    private Integer quantity;
    private String warehouse;
    private String product;
}
