package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Price extends CommonFields {
    private BigDecimal costPrice;
    private BigDecimal sellingPrice;
    private BigDecimal discountPrice;
    private BigDecimal difference;
    private BigDecimal discount;
}
