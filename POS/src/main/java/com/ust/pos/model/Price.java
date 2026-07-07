package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@SQLRestriction("deleted = false")
public class Price extends CommonFields{
    private BigDecimal costPrice;
    private BigDecimal sellingPrice;
    private BigDecimal difference;
}
