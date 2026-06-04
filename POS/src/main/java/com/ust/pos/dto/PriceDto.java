package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PriceDto extends CommonDto {
    private long sellingPrice;
    private long costPrice;
    private long difference;
}
