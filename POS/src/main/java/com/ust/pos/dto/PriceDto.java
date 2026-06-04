package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PriceDto extends CommonDto{
    private Double mrp;
    private Double sellingPrice;
    private LocalDateTime effectiveFrom;
}
