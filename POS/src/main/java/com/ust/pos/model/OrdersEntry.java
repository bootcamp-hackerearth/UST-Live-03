package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class OrdersEntry extends CommonFields {

    private String ordersId;

    private String productId;

    private String productName;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private BigDecimal discount;

    private BigDecimal totalPrice;
}