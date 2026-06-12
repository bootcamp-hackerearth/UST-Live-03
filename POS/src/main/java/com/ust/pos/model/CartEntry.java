package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class CartEntry extends CommonFields {
    private String product;
    private String cart;
    private BigDecimal discount;
    private BigDecimal unitPrice;
    private BigDecimal quantity = new BigDecimal(0);
    private BigDecimal originalPrice;
    private BigDecimal totalPrice;
}
