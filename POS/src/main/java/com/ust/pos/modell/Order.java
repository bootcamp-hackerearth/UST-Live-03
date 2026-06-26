package com.ust.pos.modell;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "pos_orders")
public class Order extends CommonFields {
    @Column(nullable = false)
    private String customerIdentifier;
    private BigDecimal originalPrice;
    private BigDecimal discount;
    private BigDecimal totalPrice;
    private String paymentMethod;
    private BigDecimal receivedAmount;
    private BigDecimal changeAmount;
}