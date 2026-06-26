package com.ust.pos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order extends CommonFields {

    private String customerId;

    private BigDecimal subtotal;

    private String paymentMethod;

    private Boolean paymentCompleted = false;
    private BigDecimal discount;

    private BigDecimal totalPrice;

    private String couponCode;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private LocalDateTime createdAt;
    private String customer;
}
