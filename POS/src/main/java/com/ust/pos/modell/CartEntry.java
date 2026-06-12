package com.ust.pos.modell;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class CartEntry extends CommonFields {
    private String cartIdentifier;

    private String productIdentifier;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal originalPrice;

    private BigDecimal discount;

    private BigDecimal totalPrice;
}