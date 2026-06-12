package com.ust.pos.model;


import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@Entity
public class Cart extends CommonFields {
    private BigDecimal discount;
    private BigDecimal totalPrice;
    private String coupon;
    private BigDecimal totalOriginalPrice;
}
