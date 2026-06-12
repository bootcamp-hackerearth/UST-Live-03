package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Stock extends CommonFields {

    private String stockStatus;
    private long quantity;
    private int minimumStock;
    private String warehouse;
    private String product;
}
