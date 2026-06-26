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
    private BigDecimal originalPrice;
    private BigDecimal discount;
    private String paymentMethod;
    private LocalDateTime orderedAt;
}