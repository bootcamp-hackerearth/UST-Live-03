package com.ust.pos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class OrderEntry extends CommonFields {
    private BigDecimal price;
    private BigDecimal totalPrice;
    private BigDecimal sellingPrice;
    private BigDecimal discount;
    private BigDecimal quantity;
    private String couponCode;
    private String product;
    @Column(name = "order_ref")
    private String order;
}