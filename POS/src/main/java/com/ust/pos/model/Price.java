package com.ust.pos.model;


import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Price extends CommonFields {
    private long costPrice;
    private long sellingPrice;
    private long difference;
}
