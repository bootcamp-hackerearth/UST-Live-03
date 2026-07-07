package com.ust.pos.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@SQLRestriction("deleted = false")
public class Warehouse extends CommonFields{
    private String country;
    private int pincode;
    private String address;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> shelves = new ArrayList<>();
}
