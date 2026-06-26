package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Orders extends CommonFields {

    private BigDecimal totalPrice;
    private BigDecimal totalOriginalPrice;
    private BigDecimal discount;

    private String coupon;
    private String couponCode;

    private String paymentType;
    private String customerId;

    private LocalDateTime orderDate = LocalDateTime.now();
}
