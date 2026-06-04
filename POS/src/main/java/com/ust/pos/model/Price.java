package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Price extends CommonFields{
    private Double mrp;
    private Double sellingPrice;
    private LocalDateTime effectiveFrom;

}
