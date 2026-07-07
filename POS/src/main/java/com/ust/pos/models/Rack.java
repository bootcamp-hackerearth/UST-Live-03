package com.ust.pos.models;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Rack extends CommonFields {
    private List<String> shelfs;
}
