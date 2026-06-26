package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class InvoiceDto extends CommonDto {
    private String orderId;
    private String cartId;
    private String paymentReference;
    private List<OrderEntryDto> entries;
    private BigDecimal originalPrice;
    private BigDecimal totalPrice;
    private BigDecimal discount;
    private LocalDateTime generatedAt;
}