package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PriceDto extends CommonDto {
    private BigDecimal amount;
    private String priceType;
    private String product;
}