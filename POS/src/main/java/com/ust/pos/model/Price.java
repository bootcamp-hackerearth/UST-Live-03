package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Price extends CommonFields {

    private String productIdentifier;
    private BigDecimal priceAmount;
    private String priceType;

}
