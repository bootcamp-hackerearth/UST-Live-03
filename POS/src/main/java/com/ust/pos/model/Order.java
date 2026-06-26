package com.ust.pos.model;

import jakarta.persistence.Entity;
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
    private String customerIdentifier;
    private BigDecimal totalPrice;
    private BigDecimal discount;
    private BigDecimal originalPrice;
    private LocalDateTime orderPlacedTime;
    private String paymentMethod;
}