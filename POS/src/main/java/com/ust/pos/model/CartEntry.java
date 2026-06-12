package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
public class CartEntry extends CommonFields {

    private String product;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String cartId;
    private BigDecimal discount;
    private BigDecimal quantity;
    private BigDecimal originalPrice;
}
