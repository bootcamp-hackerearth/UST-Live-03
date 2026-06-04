package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Stock extends CommonFields {
    private String product;
    private long quantity;
    private long reorderLevel;
    private String warehouse;
    private String rack;
    private String shelf;
}
