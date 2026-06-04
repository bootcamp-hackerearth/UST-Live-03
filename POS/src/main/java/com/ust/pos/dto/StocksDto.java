package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StocksDto extends CommonDto {
    private Long availableStock;
    private Long outgoingStock;
    private Long incomingStock;
    private String productStatus;
    private String wareHouse;
    private String name;
}