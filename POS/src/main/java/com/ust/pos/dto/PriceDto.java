package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PriceDto extends CommonDto {
    private Long costPrice;
    private Long sellingPrice;
    private Long difference;
}
