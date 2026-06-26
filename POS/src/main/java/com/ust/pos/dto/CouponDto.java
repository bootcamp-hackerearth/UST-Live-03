package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class CouponDto extends CommonDto {

    private String code;

    private String type; // PERCENT or FLAT

    private BigDecimal value;

    private LocalDateTime expiryDate;

    private Integer usageLimit;

    private Integer usedCount;

    private boolean active;

    // optional response fields
    private boolean success;
    private String message;
}