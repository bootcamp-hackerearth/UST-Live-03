package com.ust.pos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends CommonFields {
    private BigDecimal totalPrice;
    private String couponCode;
    private BigDecimal totalDiscount;
    private String orderId;
    private String paymentMode;
    private LocalDateTime orderDate;
    private String orderStatus;
}