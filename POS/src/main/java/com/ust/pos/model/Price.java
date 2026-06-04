package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Price extends CommonFields {
    private String product;
    private Double priceAmount;
    private String priceType;
}
