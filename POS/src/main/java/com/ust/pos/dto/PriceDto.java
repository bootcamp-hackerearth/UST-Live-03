package com.ust.pos.dto;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PriceDto extends CommonDto {
    private BigDecimal costPrice = new BigDecimal(0);
    private BigDecimal sellingPrice = new BigDecimal(0);
    private BigDecimal difference = new BigDecimal(0);
}