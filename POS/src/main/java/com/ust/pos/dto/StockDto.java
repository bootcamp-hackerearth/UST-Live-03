package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class StockDto extends CommonDto {
    private String product;
    private String warehouse;
    private Long quantity;
    private boolean stockStatus;
    private LocalDateTime expiryDate;
}