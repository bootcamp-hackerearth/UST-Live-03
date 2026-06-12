package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PriceDto extends CommonDto {
    private BigDecimal priceAmount;
    private String type;
    private String product;
}