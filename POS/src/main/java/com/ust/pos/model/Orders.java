package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Orders extends CommonFields {
    private BigDecimal totalPrice;
    private BigDecimal totalDiscount;
    private String couponCode;
    private String orderId;
    private LocalDateTime orderDate;
    private String paymentMode;
}