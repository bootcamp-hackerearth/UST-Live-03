package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class PriceDto extends CommonDto {
    private BigDecimal mrp;
    private BigDecimal sellingPrice;
    private BigDecimal costPrice;
    private LocalDate effectiveFrom;
}
