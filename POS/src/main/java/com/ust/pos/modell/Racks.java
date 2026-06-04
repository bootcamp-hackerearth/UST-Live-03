package com.ust.pos.modell;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Racks extends CommonFields {
    private boolean status;
    private String shelfIdentifier;
}
