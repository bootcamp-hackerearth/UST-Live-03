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

    private String customerId;

    private String customerName;

    private Long customerPhone;

    private BigDecimal totalOriginalPrice;

    private BigDecimal discount;

    private BigDecimal totalPrice;

    private String couponCode;

    private String paymentType;

    private LocalDateTime orderDate;

    private String status;
}