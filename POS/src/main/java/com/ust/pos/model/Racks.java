package com.ust.pos.model;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Racks extends  CommonFields{
    @ElementCollection
    private List<String> shelfs;
}
