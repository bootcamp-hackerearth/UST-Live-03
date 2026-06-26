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
    private BigDecimal totalDiscount;
    private String couponCode;
    private String orderId;
    private LocalDateTime orderDate;
    private String paymentMode;
}
