package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PriceDto extends CommonDto {
    private String product;
    private String type;
    private Long amount;
    private String currency;
}