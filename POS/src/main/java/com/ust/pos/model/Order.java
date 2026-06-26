package com.ust.pos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order extends CommonFields {
    private BigDecimal totalPrice;
    private BigDecimal discount;
    private String coupon;
    private BigDecimal totalOriginalPrice;
    private LocalDateTime orderDate = LocalDateTime.now();
    private String paymentType;
    private String customerId;
    private String couponCode;
}