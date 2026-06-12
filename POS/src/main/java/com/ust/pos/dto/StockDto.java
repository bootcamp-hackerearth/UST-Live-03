package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class StockDto extends CommonDto {
    private String product;
    private String warehouse;
    private Long quantity;
    private LocalDateTime expiryDate;
    private List<String> shelf;
    private List<String> rack;
}