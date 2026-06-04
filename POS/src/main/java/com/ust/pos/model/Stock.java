package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Stock extends CommonFields {

    private List<String> warehouse;
    private Integer quantity;
    private Double unitPrice;
}
