package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class OrderDto extends CommonDto {
    private String customerIdentifier;
    private BigDecimal originalPrice;
    private BigDecimal discount;
    private BigDecimal totalPrice;
    private List<OrderEntryDto> entryList;
    private String paymentMethod;
    private BigDecimal receivedAmount;
    private BigDecimal changeAmount;
}
