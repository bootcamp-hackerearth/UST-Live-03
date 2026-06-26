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
@Table(name = "pos_order_entries")
public class OrderEntry extends CommonFields {
    @Column(nullable = false)
    private String orderIdentifier;
    @Column(nullable = false)
    private String productIdentifier;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal originalPrice;
    private BigDecimal discount;
    private BigDecimal totalPrice;
}