package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Setter
@Getter
public class PriceDto extends CommonDto {

    private Long productId;
    private String productName;
    private BigDecimal sellingPrice;
    private BigDecimal costPrice;
    private BigDecimal mrpPrice;
}

