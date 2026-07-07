package com.ust.pos.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class WarehouseDto extends CommonDto{
    private String country;
    private int pincode;
    private String address;
    private List<String> shelves = new ArrayList<>();
}
