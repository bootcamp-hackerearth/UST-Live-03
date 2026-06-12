package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Cart extends CommonFields {

    private BigDecimal discount;
    private BigDecimal totalPrice;
    private BigDecimal originalPrice;
    private String coupon;

}
