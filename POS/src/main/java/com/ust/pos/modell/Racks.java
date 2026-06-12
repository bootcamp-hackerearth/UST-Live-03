package com.ust.pos.modell;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Racks extends CommonFields {
    private List<String> shelfs;
}