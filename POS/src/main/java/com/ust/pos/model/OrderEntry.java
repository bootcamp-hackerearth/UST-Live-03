package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class OrderEntry extends CommonFields {
    private String product;
    private BigDecimal discount;
    private BigDecimal totalPrice;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalOriginalPrice;
    private String orderId;
}