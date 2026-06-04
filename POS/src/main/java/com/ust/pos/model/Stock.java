package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Stock extends CommonFields {
    private String stockStatus;
    private long quantity;
    private String warehouseName;

}
