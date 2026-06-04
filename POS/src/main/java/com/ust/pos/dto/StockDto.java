package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StockDto extends CommonDto {
    private Long quantity;
    private Boolean status;
    private String product;
    private String warehouse;
}
