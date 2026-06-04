package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockDto extends CommonDto {
    private String product;
    private Long quantity;
    private String stockStatus;
    private String warehouse;
}
