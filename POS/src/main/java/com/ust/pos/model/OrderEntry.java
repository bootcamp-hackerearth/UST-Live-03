package com.ust.pos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "order_entries")
public class OrderEntry extends CommonFields {

    private String orderId;
    private String product;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal sellingPrice;
    private BigDecimal discount;
    private BigDecimal totalPrice;
    private String couponCode;
    private String cart;
}