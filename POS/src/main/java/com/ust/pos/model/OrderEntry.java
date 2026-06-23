package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class OrderEntry extends CommonFields {
    private String orderIdentifier;
    private String product;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal sellingPrice;
    private BigDecimal discount;
    private BigDecimal totalPrice;
    private String couponCode;
}