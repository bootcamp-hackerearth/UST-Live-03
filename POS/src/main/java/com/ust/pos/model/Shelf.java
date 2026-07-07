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
@Getter
@Setter
@SQLRestriction("deleted = false")
public class Shelf extends CommonFields{
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> racks = new ArrayList<>();
}
