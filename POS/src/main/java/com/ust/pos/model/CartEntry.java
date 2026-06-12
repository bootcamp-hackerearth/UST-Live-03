package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class CartEntry extends CommonFields{
    private BigDecimal price;
    private BigDecimal totalPrice;
    private BigDecimal sellingPrice;
    private BigDecimal discount;
    private BigDecimal quantity;
    private String couponCode;
    private String product;
    private String cart;
}
