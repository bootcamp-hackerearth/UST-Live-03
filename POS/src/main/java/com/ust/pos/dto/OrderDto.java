package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderDto extends CommonDto {
    private String customer;
    private String customerName;
    private Long customerPhone;
    private Double totalOriginalPrice;
    private Double discount;
    private Double totalPrice;
    private LocalDateTime orderDate;
    private String status;
    private List<OrderEntryDto> orderEntryDtoList;
}