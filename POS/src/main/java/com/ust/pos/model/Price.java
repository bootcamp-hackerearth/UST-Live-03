package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Price extends CommonFields {

    private Double costPrice;
    private String product;
    private String priceType;
}
