package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Stock extends CommonFields {
    private String product;
    private String warehouse;
    private Long quantity;
    private LocalDateTime expiryDate;
}
