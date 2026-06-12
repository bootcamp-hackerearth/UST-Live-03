package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class PriceDto extends CommonDto {
    private String productIdentifier;
    private BigDecimal mrp;
    private BigDecimal sellingPrice;
    private BigDecimal costPrice;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate effectiveFrom;
}
