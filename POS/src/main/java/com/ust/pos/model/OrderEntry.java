package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class OrderEntry extends CommonFields {

    private String orderIdentifier;
    private String productIdentifier;
    private Integer quantity;
    private BigDecimal sellingPrice;
    private BigDecimal mrpPrice;
    private BigDecimal totalPrice;
    private BigDecimal discount;
}