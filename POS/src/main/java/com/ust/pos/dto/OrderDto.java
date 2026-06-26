package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderDto extends CommonDto {

    private BigDecimal totalPrice;
    private BigDecimal originalPrice;
    private BigDecimal discount;
    private String paymentMethod;
    private LocalDateTime orderedAt;
    private List<OrderEntryDto> entries;
}