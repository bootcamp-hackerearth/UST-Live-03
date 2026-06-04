package com.ust.pos.modell;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Brand extends CommonFields {
    @Column(length = 500)
    private String description;
}
