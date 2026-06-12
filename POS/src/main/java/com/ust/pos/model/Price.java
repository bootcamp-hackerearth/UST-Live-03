package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class Price extends CommonFields {

    private String product;
    private BigDecimal priceAmount;
    private String priceType;
}
