package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Stock extends CommonFields {
    private Integer minimumstock;
    private Integer quantity;
    private String warehouse;
    private String product;
}