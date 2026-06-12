package com.ust.pos.modell;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Role extends CommonFields {
    private String description;
}