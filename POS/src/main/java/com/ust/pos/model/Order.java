package com.ust.pos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order extends CommonFields {
    private String customer;
    private BigDecimal totalPrice;
    private BigDecimal totalDiscount;
    private String couponCode;
    private String orderStatus;
    private String orderId;
}