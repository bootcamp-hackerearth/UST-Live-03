package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PriceDto extends CommonDto {
    private BigDecimal costPrice; // 120
    private BigDecimal sellingPrice;// 150
    private BigDecimal mrp; // //150
}
