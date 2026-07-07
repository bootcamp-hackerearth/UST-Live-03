package com.ust.pos.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@SQLRestriction("deleted = false")
public class Product extends CommonFields{
    private String warehouseName;
    private Long supplierId;
    private String category;
    private String unit;
}
