package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PriceDto extends CommonDto {
    private String productId;
    private String productName;
    private String priceType;
    private BigDecimal value;
}

