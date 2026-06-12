package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Cart extends CommonFields {
    private BigDecimal totalPrice;//sum of all prices after discount
    private BigDecimal totalDiscount;//
    private BigDecimal totalOriginalPrice;//sum of og price of all entries
    private String coupon;
}
