package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class CartEntry extends CommonFields {
    private String product;
    private BigDecimal discount;
    private BigDecimal totalPrice;
    private BigDecimal originalPrice;
    private String cartId;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
}
