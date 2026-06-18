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

    private String orderId;
    private String cartIdentifier;
    private String customerIdentifier;
    private String paymentMode;
    private LocalDateTime orderDate;
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private BigDecimal discount = BigDecimal.ZERO;
    private BigDecimal taxAmount = BigDecimal.ZERO;
    private BigDecimal grandTotal = BigDecimal.ZERO;
    private String coupon;
}