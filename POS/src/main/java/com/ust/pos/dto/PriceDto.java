package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PriceDto extends CommonDto{
    private BigDecimal mrp;
    private BigDecimal sellingPrice;
    private LocalDateTime effectiveFrom;
    private BigDecimal costPrice;
}
