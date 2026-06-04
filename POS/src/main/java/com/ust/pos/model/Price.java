package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Price extends CommonFields {

    private BigDecimal mrp;
    private BigDecimal sellingPrice;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate effectiveFrom;

}
