package com.ust.pos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_entry")
@Getter
@Setter
public class CartEntry extends CommonFields {

    private String cartIdentifier;
    private String productIdentifier;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal mrp;
    private BigDecimal originalPrice;
    private BigDecimal discount;
    private BigDecimal unitDiscount;
    private BigDecimal totalPrice;

}
