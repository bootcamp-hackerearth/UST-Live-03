package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter

public class CartEntry extends CommonFields {
    private String productId;
    private String cartId;
    private BigDecimal quantity = new BigDecimal(0);
    private BigDecimal mrp;
    private BigDecimal sellingPrice;
    private BigDecimal discount;
    private BigDecimal totalPrice;
    private BigDecimal originalPrice;
}
