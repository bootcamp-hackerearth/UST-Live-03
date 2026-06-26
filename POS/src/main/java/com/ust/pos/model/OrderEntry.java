package com.ust.pos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "order_entry")
public class OrderEntry extends CommonFields {
    private String orderIdentifier;

    private String productIdentifier;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private BigDecimal originalPrice;

    private BigDecimal discount;

    private BigDecimal totalPrice;
}