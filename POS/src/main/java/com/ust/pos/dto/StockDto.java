package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockDto extends CommonDto {
    private String product;
    private String warehouse;
    private Long quantity;
    private boolean status;
}
