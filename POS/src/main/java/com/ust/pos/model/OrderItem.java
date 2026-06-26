package com.ust.pos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "order_items")
public class OrderItem extends CommonFields {

    private String orderIdentifier;

    private String product;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalPrice;
}