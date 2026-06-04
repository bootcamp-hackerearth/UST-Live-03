package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Price extends CommonFields {
    private String productId;
    private String productName;
    private String priceType;
    private BigDecimal value;
}