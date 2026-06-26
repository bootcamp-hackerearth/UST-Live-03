package com.ust.pos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "order_details")
public class Order extends CommonFields {
    private String customerIdentifier;
    private BigDecimal originalPrice;
    private BigDecimal discount;
    private BigDecimal totalPrice;
    private String paymentMethod;
    private BigDecimal receivedAmount;
    private BigDecimal changeAmount;
}
