package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderDto extends CommonDto {
    private String customerIdentifier;
    private BigDecimal totalPrice;
    private BigDecimal discount;
    private BigDecimal originalPrice;
    private LocalDateTime orderPlacedTime;
    private String paymentMethod;
    private List<OrderEntryDto> entryList;
}