package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Setter
@Getter
public class OrderEntry extends CommonFields {

    private String product;
    private BigDecimal discount;
    private BigDecimal totalPrice;
    private BigDecimal unitPrice;
    private BigDecimal quantity;
    private BigDecimal totalOriginalPrice;
    private String orderId;
}
