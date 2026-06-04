package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Stock extends CommonFields {

    private Integer quantity;
    private String stockStatus;
    private String warehouse;
    private String unit;

}
