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

    private String customerEmail;

    private String shippingAddress;

    private String paymentMethod;

    private LocalDateTime paymentTime;

    private BigDecimal subtotal;

    private BigDecimal discount;

    private BigDecimal total;
}