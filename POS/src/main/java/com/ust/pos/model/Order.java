package com.ust.pos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Table(name = "orders")
@Entity
@Getter
@Setter
public class Order extends CommonFields {
    private String invoiceCode;
    private String customerIdentifier;
    private String warehouseIdentifier;

    private BigDecimal originalPrice;
    private BigDecimal totalDiscount;
    private BigDecimal totalPrice;

    private BigDecimal amountReceived;
    private BigDecimal changeAmount;
    private BigDecimal dueAmount;

    private String paymentMethod;
    private String transactionNotes;
    private String couponCode;
}