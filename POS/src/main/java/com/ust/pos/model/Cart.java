package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Cart extends CommonFields {

    private String username;
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private BigDecimal discount = BigDecimal.ZERO;
    private String coupon;
}