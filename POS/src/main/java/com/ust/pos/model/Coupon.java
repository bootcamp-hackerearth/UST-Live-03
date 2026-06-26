package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Coupon extends CommonFields {

    private String code;

    private String type;

    private BigDecimal value;

    private LocalDateTime expiryDate;

    private Integer usageLimit;

    private Integer usedCount = 0;

    private boolean active = true;
}