package com.ust.pos.modell;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Price extends CommonFields {
    private BigDecimal priceAmount;
    private String type;
    private String product;

}
