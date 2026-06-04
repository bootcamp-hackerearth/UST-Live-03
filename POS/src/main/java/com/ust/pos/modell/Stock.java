package com.ust.pos.modell;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Stock extends CommonFields {
    @Column(nullable = false)
    private Long productId;
    @Column(nullable = false)
    private Long warehouseId;
    private String productIdentifier;
    private String warehouseIdentifier;
    private Integer quantity;
    private Integer minimumStock;
    private boolean status;
}

